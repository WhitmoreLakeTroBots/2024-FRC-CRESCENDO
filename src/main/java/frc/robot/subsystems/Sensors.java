package frc.robot.subsystems;

//import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import frc.robot.commands.intakeCommands.intakeCmd;
import frc.robot.commands.intakeCommands.pivotCmd;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 *
 */
public class Sensors extends SubsystemBase {

    public DigitalInput IntakeSen;

    /**
    *
    */
    public Sensors() {
        IntakeSen = new DigitalInput(0);
        //addChild("Digital Input 1" , BeamBreak1);

    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        checkIntake();
    
    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

public boolean getBB1(){
    return !IntakeSen.get();
}

public void checkIntake(){
    if (RobotContainer.getInstance().m_Intake.getTargPivotPos() == Intake.PivotPos.OUT){
        if (getBB1()){
            new intakeCmd(Intake.RollerStatus.STOP);
            new pivotCmd(Intake.PivotPos.IN, false);
    }
    }
    
}

}
