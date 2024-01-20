package frc.robot.subsystems;


//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;


/**
 *
 */
public class Intake extends SubsystemBase {
    
    private RollerStatus CRollerStatus = RollerStatus.STOP;



private PWMSparkMax rotMotor;

    
    /**
    *
    */
    public Intake() {
rotMotor = new PWMSparkMax(1);
 addChild("rotMotor",rotMotor);
 rotMotor.setInverted(false);


    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run

    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    public void setRollerStatus(RollerStatus newStatus){
        switch (newStatus) {
            case STOP:
                
                break;
            case FORWARD:

                break;
            case REVERSE:
                
                break;
            default:
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

}

