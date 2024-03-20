package frc.robot.subsystems;

//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.Constants.CANIDs;
import frc.robot.subsystems.Lighting.lightPattern;
import frc.robot.subsystems.Vibration.VIBALERT;
import frc.utils.CommonLogic;
import frc.utils.RobotMath;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.SparkAbsoluteEncoder.Type;

/**
 *
 */
public class Intake extends SubsystemBase {

    private RollerStatus CRollerStatus = RollerStatus.STOP;
    private PivotPos targetPivotPos = PivotPos.START;
    private double pivPosTol = 10;

    private CANSparkMax rotMotor;
    private CANSparkMax pivMotor;
    private double pivP = 0.0; // now controlled by position
    private double pivF = 0.0; // now controlled by position

    private double minPivPower = -0.85;
    private double maxPivPower = 0.85;

    public double startTime = 0;
    public double endTime = 0;
    public double delayTime = 0.4;
    public double delayTime2 = 0.3;
    public boolean justDetected = false;

    /**
    *
    */
    public Intake() {
        rotMotor = new CANSparkMax(CANIDs.RotMotorId, CANSparkMax.MotorType.kBrushless);
        CommonLogic.setSparkParamsBase(rotMotor, false, 30, 40, IdleMode.kCoast);

        pivMotor = new CANSparkMax(CANIDs.PivMotorId, CANSparkMax.MotorType.kBrushless);
        pivMotor.getAbsoluteEncoder().setPositionConversionFactor(360);
        CommonLogic.setSparkParamsBase(pivMotor, false, 40, 40, IdleMode.kBrake);

    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        pivMotor.set(CommonLogic.CapMotorPower(
                CommonLogic.gotoPosPIDF(pivP, pivF, getCurPivotPos(), targetPivotPos.pos),
                minPivPower, maxPivPower));
    
        if ((getCurRollerStatus() == RollerStatus.FORWARD) && (Math.abs(rotMotor.getAppliedOutput()) > 0.0)
         && (RobotContainer.getInstance().m_Sensors.getBB1() == true)) {
           
            if(!justDetected){
                    startTime = RobotMath.getTime();
                    endTime = startTime + delayTime;
                    justDetected = true;
                    setPivotPos(PivotPos.IN);
                    RobotContainer.getInstance().m_Vibration.StartVib(VIBALERT.DRIVERLONG);
                    RobotContainer.getInstance().m_Vibration.StartVib(VIBALERT.ARTICLONG);

                RobotContainer.getInstance().m_Lighting.setNewBaseColor(lightPattern.ORANGE);


            }

        if (RobotMath.getTime() >= endTime) {
             rotMotor.set(0);
            setRollerStatus(RollerStatus.STOP);
        }
        }
        else{
            justDetected = false;
        }

         if ((getCurRollerStatus() == RollerStatus.REVERSE) && (Math.abs(rotMotor.getAppliedOutput()) != 0.0)
         && (RobotContainer.getInstance().m_Sensors.getBB1() == false)&& 
            (RobotContainer.getInstance().m_Intake.getTargPivotPos() == PivotPos.AMP)) {
           
            if(justDetected){
                    startTime = RobotMath.getTime();
                    endTime = startTime + delayTime2;
                    justDetected = false;
            }

        if (RobotMath.getTime() >= endTime) {
             rotMotor.set(0);
            setRollerStatus(RollerStatus.STOP);
            setPivotPos(PivotPos.IN);

        }
        }
        

    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    public void setPivotPos(PivotPos newPos) {
        targetPivotPos = newPos;
        pivP = targetPivotPos.P;
        pivF = targetPivotPos.F;
    }

    public void setRollerStatus(RollerStatus newStatus) {
        switch (newStatus) {
            case STOP:
                rotMotor.set(newStatus.pow);
                CRollerStatus = newStatus;
                break;

            case FORWARD:
                rotMotor.set(newStatus.pow);
                CRollerStatus = newStatus;
                break;

            case REVERSE:
                rotMotor.set(newStatus.pow);
                CRollerStatus = newStatus;
                break;

            default:
                rotMotor.set(0);
                break;
        }
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public RollerStatus getCurRollerStatus() {
        return CRollerStatus;
    }

    public boolean getPivotStatus(){
        return(CommonLogic.isInRange(getCurPivotPos(), targetPivotPos.pos, pivPosTol));
    }

    public double getCurPivotPos(){
        return pivMotor.getAbsoluteEncoder(Type.kDutyCycle).getPosition();
    }

    public PivotPos getTargPivotPos() {
        return targetPivotPos;
    }

    public enum RollerStatus {
        STOP(0.0),
        FORWARD(0.7),
        REVERSE(-0.85);

        private final double pow;

        public double getPow() {
            return pow;
        }

        RollerStatus(double pow) {
            this.pow = pow;
        }
    }

    public double pivNormalize(double heading) {
        // takes the full turns out of heading
        // gives us values from 0 to 180 for the right side of the robot
        // and values from 0 to -179 degrees for the left side of the robot
        double degrees = heading % 360;

        if (degrees > 180) {
          degrees = degrees - 360;
        }
        if (degrees < -179) {
          degrees = degrees + 360;
        }
        return degrees;
      }

    public enum PivotPos {
        START(1.7, 0.0027, 0.0),
        IN(9.5, 0.0025, 0.0),
        OUT(205, 0.003, 0.0),

        AMP(78, 0.003, 0.0);

        private final double pos;
        private final double P;
        private final double F;

        public double getPos() {
            return pos;
        }
        public double getP() {
            return P;
        }
        public double getF() {
            return F;
        }

        PivotPos(double pos, double P, double F){
            this.pos = pos;
            this.P = P;
            this.F = F;
        }
    }

}
