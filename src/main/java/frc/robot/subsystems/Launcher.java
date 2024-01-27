package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;

//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
   

/**
 *
 */
public class Launcher extends SubsystemBase {
 public double kP, kI, kD, kIz, kFF, maxRPM, minVEL;
    public final double kMaxOutput = 1.0;
    public final double kMinOutput = 0.0;
    public PID PIDcalc;
    private double iTargetRPM = 0;
    private double iActualRPM = 0;
    private boolean bAutoRPMEnabled = false;
    public CANSparkMax CanSpark_launcher;
    private double currRequestedPower = 0.0; // current power requests
    private double currActualPower = 0.0;
    private double currPowerStep = 0; // how large of steps to take for ramping
    private double PIDv = 0;
    private final double LAUNCHER_MAX_RPM = 5676;
    private final double POWER_STEP_INCREMENT = .0211;
    private final double STEP_RANGE = POWER_STEP_INCREMENT + .01;
    private final int MAX_STEP_COUNT = 25;
    private int currPowerStepCounter = 0;

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

    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
            iActualRPM = CanSpark_launcher.getVelocity();

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
        CanSpark_launcher.set(currActualPower);
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

}
