package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkAbsoluteEncoder.Type;
import com.revrobotics.CANSparkBase.IdleMode;

import frc.robot.Constants;
import frc.robot.subsystems.Intake.PivotPos;
import frc.utils.*;

//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
   

/**
 *
 */
public class Launcher extends SubsystemBase {
 private double kP, kI, kD, kIz, kFF, maxRPM, minVEL;
    public final double kMaxOutput = 1.0;
    public final double kMinOutput = 0.0;
    public PID PIDcalc;
    private double iTargetRPM = 0;
    private double iActualRPM = 0;
    private boolean bAutoRPMEnabled = false;
    private CANSparkMax LaunchMotorTop;
    private CANSparkMax LaunchMotorBottom;
    private CANSparkMax FeederMotor;
    private CANSparkMax LaunchAngleMotor;
    private double currRequestedPower = 0.0; // current power requests
    private double currActualPower = 0.0;
    private double currPowerStep = 0; // how large of steps to take for ramping
    private double PIDv = 0;
    private final double LAUNCHER_MAX_RPM = 5676;
    private final double POWER_STEP_INCREMENT = .0211;
    private final double STEP_RANGE = POWER_STEP_INCREMENT + .01;
    private final int MAX_STEP_COUNT = 25;
    private int currPowerStepCounter = 0;
    private double FeederMotorOffset = 1.0;
    private double pivP = 0.03;
    private double pivF = 0.0;
    private double angleMaxPow = 0.5;
    private double angleMinPow = -0.5;
    private ANGLEPOS curAnglePos = ANGLEPOS.PRESTART;
    private double angleMotorTol = 3.0;
    private double maxPosition = 65.0; // Don't go past this

    // private double rampWaitEndTime = 0.0;
    // private final double rampWaitTime = .5;

    public enum LauncherModes {
        RAMPING,
        // RAMP_WAIT,
        RUNNING,
        STOPPED
    }

    private LauncherModes currLauncherMode = LauncherModes.STOPPED;
    /**
    *
    */
    public Launcher() {
        LaunchMotorTop = new CANSparkMax(Constants.CANIDs.LauncherMotorTopId, CANSparkMax.MotorType.kBrushless);
        LaunchMotorBottom = new CANSparkMax(Constants.CANIDs.LauncherMotorBottomId, CANSparkMax.MotorType.kBrushless);
        CommonLogic.setSparkParamsBase(LaunchMotorTop, true, 40, 40, IdleMode.kCoast);
        CommonLogic.setSparkParamsBase(LaunchMotorBottom, false, 40, 40, IdleMode.kCoast);

        kP = 0.0;
        kI = 0.0;
        kD = 0.0;
        kIz = 0;
        kFF = 0;
        PIDcalc = new PID(kP, kI, kD);

        FeederMotor = new CANSparkMax(Constants.CANIDs.FeederMotorId, CANSparkMax.MotorType.kBrushless);
        CommonLogic.setSparkParamsBase(FeederMotor, false, 20, 30, IdleMode.kCoast);

        LaunchAngleMotor = new CANSparkMax(Constants.CANIDs.LaunchAngleMotorId , CANSparkMax.MotorType.kBrushless);
        LaunchAngleMotor.getAbsoluteEncoder().setPositionConversionFactor(360);
        CommonLogic.setSparkParamsBase(LaunchAngleMotor, false, 40, 50, IdleMode.kBrake);

    }

    private void setLaunchPwr(double pwr){
        LaunchMotorTop.set(pwr);
        LaunchMotorBottom.set(pwr);
        FeederMotor.set(pwr*FeederMotorOffset);
    
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
            iActualRPM = LaunchMotorTop.getEncoder().getVelocity();

            LaunchAngleMotor.set(CommonLogic.CapMotorPower(
                CommonLogic.gotoPosPIDF(pivP, pivF, getAnglePosActual(), curAnglePos.pos),
                angleMinPow, angleMaxPow));

                   switch (currLauncherMode)
        {
            case RAMPING:
                // we are ramping to corret power
                // Ramping ends if our power is near where we want it OR if we have steped
                // a given number of times ~20ms * 25 ~ 500 ms
                if (CommonLogic.isInRange(currActualPower, currRequestedPower, STEP_RANGE) ||
                        currPowerStepCounter >= MAX_STEP_COUNT) {
                    // We are close to running speed go to pid control
                    setpower(currRequestedPower);
                    currLauncherMode = LauncherModes.RUNNING;
                    // rampWaitEndTime = CommonLogic.getTime() + rampWaitTime;
                    PIDcalc.resetErrors();
                } else {
                    // We are not close to requested velocity keep ramping it
                    setpower(currActualPower + currPowerStep);
                }
                break;
            /*
             * case RAMP_WAIT:
             * if (CommonLogic.getTime() >= rampWaitEndTime) {
             * PIDcalc.resetErrors();
             * currLauncherMode = LauncherModes.RUNNING;
             * }
             * break;
             */
            case RUNNING:
                // we are running under PID Control
                PIDv = calcpowrdiff(iActualRPM, iTargetRPM);
                setpower(PIDv + currRequestedPower);
                break;
            case STOPPED:
                // We are stopped and waiting for new RPM to run to
                iTargetRPM = 0;
                setpower(0.0);
                PIDcalc.resetErrors();
                break;
            default:
                // This should never happen
                currLauncherMode = LauncherModes.STOPPED;
        }


    }

   public void setTargetRPM(Double newTargetRPM) {

        // Saftey check if setting to 0 then just call stop and quit
        if (CommonLogic.isInRange(newTargetRPM, 0.0, 5)) {
            stop();
            return;
        }
        iTargetRPM = newTargetRPM;
        currRequestedPower = iTargetRPM / LAUNCHER_MAX_RPM;
        currLauncherMode = LauncherModes.RAMPING;
        currPowerStepCounter = 0;
        currPowerStep = Math.signum(currRequestedPower - currActualPower) * POWER_STEP_INCREMENT;
    }

    public double getTargetRPM() {
        return iTargetRPM;
    }

    public double getActualRPM() {
        return iActualRPM;
    }

    private void setpower(double power) {
        // be sure to cap the power between 0.0 (stopped) and 1.0;
        currActualPower = CommonLogic.CapMotorPower(power, kMinOutput, kMaxOutput);
        setLaunchPwr(currActualPower);
        currPowerStepCounter = currPowerStepCounter + 1;
    }

    // Stop the flywheel
    public void stop() {
        iTargetRPM = 0.0;
        bAutoRPMEnabled = false;
        currRequestedPower = 0.0;
        setpower(currRequestedPower);
        currLauncherMode = LauncherModes.STOPPED;
    }

    @Override
    public void simulationPeriodic() {

    }

    public double getPIDv() {
        return PIDv;
    }

    private double calcpowrdiff(Double curentSpeed, double targetSpeed) {
        return PIDcalc.calcPID(targetSpeed, curentSpeed) / LAUNCHER_MAX_RPM;

    }

    // IS the launcher RPM in a small tight range of values
    public boolean IsVelocityInTol(double percent) {
        double pcnt = CommonLogic.CapMotorPower(Math.abs(percent) / 100, 0.0, 1.0);

        return (CommonLogic.isInRange(iActualRPM, iTargetRPM, (iTargetRPM * pcnt)) /*
                                                                                    * &&
                                                                                    * LauncherModes.RUNNING ==
                                                                                    * currLauncherMode
                                                                                    */);
    }

    // Enable/Disable the AutoRPM Logic
    public boolean AutoRPM_get() {
        return bAutoRPMEnabled;
    }

    public void AutoRPM_set(boolean newValue) {
        bAutoRPMEnabled = newValue;
    }

    public void AutoRPM_toggle() {
        bAutoRPMEnabled = !bAutoRPMEnabled;
    }

    public boolean getAngleStatus(){
        return(CommonLogic.isInRange(getAnglePosActual(), curAnglePos.pos, angleMotorTol));
    }

      public void setAnglePos(ANGLEPOS newPos) {
        curAnglePos = newPos;
        setTargetRPM(curAnglePos.RPM);
    }

    public ANGLEPOS getAnglePos(){
        return curAnglePos;
    }

    public double getAnglePosActual(){
        return LaunchAngleMotor.getAbsoluteEncoder(Type.kDutyCycle).getPosition() ;
    }

    public enum ANGLEPOS{
        MAX(56,61,0),
        PRESTART(55, 60, 0),
        // Auton Angles
        CENTERNOTE(40,45,3500),

        TOPNOTEWING(50, 55, 2500),
        TOPLAUNCH(25, 30, 3500),
        APODIUM(35, 40, 3000),
        // Standard Angles
        START(25.0, 30.0, 0), // DDown //B
        TEST(30.0, 35.0, 0),
        UNDERSPEAKER(60.0, 65, 2500), //DUp
        AMP(55.0, 60, 800), //A
        PASS(50,55, 1700),
        MIDRANGE(27, 32, 4000), //DRight
        PODIUM(36,41,3000), //DLeft
        THROW(12, 17, 2500);
        

        private final double angle;
        private final double pos;
        private final double RPM;

    public double getangle(){
        return angle;
    }
    public double getpos(){
        return pos;
    }
    public double getRPM(){
        return RPM;
    }

ANGLEPOS(double angle, double pos, double RPM){
    this.angle = angle;
    this.pos = pos;
    this.RPM = RPM;
}

    }
    

}
