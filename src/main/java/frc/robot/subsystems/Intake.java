package frc.robot.subsystems;


//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CANIDs;
import frc.utils.CommonLogic;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;


/**
 *
 */
public class Intake extends SubsystemBase {
    
    private RollerStatus CRollerStatus = RollerStatus.STOP;
    private PivotPos targetPivotPos = PivotPos.START;



private CANSparkMax rotMotor;
private CANSparkMax pivMotor;

private double rotForwardP = 0.4;
private double rotBackP = -0.4;
    
    /**
    *
    */
    public Intake() {
rotMotor = new CANSparkMax(CANIDs.RotMotorId, CANSparkMax.MotorType.kBrushless);
 CommonLogic.setSparkParamsBase(rotMotor, false,10, 30, IdleMode.kCoast);
 

 pivMotor = new CANSparkMax(CANIDs.PivMotorId, CANSparkMax.MotorType.kBrushless);
 CommonLogic.setSparkParamsBase(pivMotor, false,10, 30, IdleMode.kBrake);

    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run

    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    public void setPivotPos(PivotPos newPos){
        
    }

    public void setRollerStatus(RollerStatus newStatus){
        switch (newStatus) {
            case STOP:
                rotMotor.set(0);
                CRollerStatus = newStatus;
                break;

            case FORWARD:
                rotMotor.set(rotForwardP);
                CRollerStatus = newStatus;
                break;

            case REVERSE:
                rotMotor.set(rotBackP);
                CRollerStatus = newStatus;
                break;

            default:
                rotMotor.set(0);
                break;
        }
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public enum RollerStatus{
        STOP,
        FORWARD,
        REVERSE;
    }

    public enum PivotPos{
        START(0.0),
        IN(0.0),
        OUT(0.0);
        private final double pos;
        public double getPos(){
            return pos;
        }
        PivotPos(double pos){
            this.pos = pos;
        }
    }

}

