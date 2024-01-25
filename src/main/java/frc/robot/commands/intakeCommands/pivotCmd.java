package frc.robot.commands.intakeCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Intake;

//import java.util.function.DoubleSupplier;

/**
 *
 */
public class pivotCmd extends Command {
    private boolean bDone = false;
    private boolean bWait = false;
    private Intake.PivotPos nPivotPos;

    public pivotCmd(Intake.PivotPos newPivotPos, boolean Wait) {
        nPivotPos = newPivotPos;
        bWait = Wait;

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        RobotContainer.getInstance().m_Intake.setPivotPos(nPivotPos);
        if(!bWait){
            bDone = true;
            end(false);
        }
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        if(RobotContainer.getInstance().m_Intake.getPivotStatus()){
            bDone = true;
            end(false);
        }
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean runsWhenDisabled() {
        return false;

    }
}
