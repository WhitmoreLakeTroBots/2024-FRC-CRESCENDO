package frc.robot.subsystems;

//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CANIDs;
import frc.utils.CommonLogic;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.SparkAbsoluteEncoder.Type;

/**
 *
 */
public class Intake extends SubsystemBase {

    private RollerStatus CRollerStatus = RollerStatus.STOP;
    private PivotPos targetPivotPos = PivotPos.START;
    private double pivPosTol = 0.1;

    private CANSparkMax rotMotor;
    private CANSparkMax pivMotor;
    private double pivP = 0.1;
    private double pivF = 0.0;

    private double minPivPower = -0.2;
    private double maxPivPower = 0.2;

    /**
    *
    */
    public Intake() {
        rotMotor = new CANSparkMax(CANIDs.RotMotorId, CANSparkMax.MotorType.kBrushless);
        CommonLogic.setSparkParamsBase(rotMotor, false, 10, 30, IdleMode.kCoast);

        pivMotor = new CANSparkMax(CANIDs.PivMotorId, CANSparkMax.MotorType.kBrushless);
        CommonLogic.setSparkParamsBase(pivMotor, false, 10, 30, IdleMode.kBrake);

    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        pivMotor.set(CommonLogic.CapMotorPower(
                CommonLogic.gotoPosPIDF(pivP, pivF, pivMotor.getEncoder().getPosition(), targetPivotPos.pos),
                minPivPower, maxPivPower));
    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    public void setPivotPos(PivotPos newPos) {
        targetPivotPos = newPos;
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
        return(CommonLogic.isInRange(pivMotor.getEncoder().getPosition(), targetPivotPos.pos, pivPosTol));
    }

    public double getCurPivotPos(){
        return pivMotor.getAbsoluteEncoder(Type.kDutyCycle).getPosition();
    }

    public PivotPos getTargPivotPos() {
        return targetPivotPos;
    }

    public enum RollerStatus {
        STOP(0.0),
        FORWARD(0.4),
        REVERSE(-0.4);

        private final double pow;

        public double getPow() {
            return pow;
        }

        RollerStatus(double pow) {
            this.pow = pow;
        }
    }

    public enum PivotPos {
        START(0.0),
        IN(0.0),
        OUT(0.89);

        private final double pos;

        public double getPos() {
            return pos;
        }

        PivotPos(double pos) {
            this.pos = pos;
        }
    }

}
