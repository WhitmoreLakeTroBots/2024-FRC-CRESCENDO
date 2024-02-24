package frc.robot.commands.climbCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Climb.ClimbMode;

public class ClimbCmd extends Command {
    private boolean bDone = false;
    private ClimbMode newClimbMode;
    public ClimbCmd(ClimbMode nClimbMode) {
        newClimbMode = nClimbMode;
        
        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
    // if fixedDist = false => stagPosition is suposed to recieve the percantage to
    // be traversed in stag, in 0.xx format

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {

        RobotContainer.getInstance().m_Climb.setClimbMode(newClimbMode);
       
        bDone = true;
        end(bDone);
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        bDone = true;
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        bDone = true;
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return bDone;
    }

    @Override
    public boolean runsWhenDisabled() {
        return false;

    }
}
