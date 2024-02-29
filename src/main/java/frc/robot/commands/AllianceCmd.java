package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;

public class AllianceCmd extends Command {
    private boolean bDone = false;
    private boolean alliance = false;
    public AllianceCmd(boolean AL) {
        alliance = AL;
        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
    // if fixedDist = false => stagPosition is suposed to recieve the percantage to
    // be traversed in stag, in 0.xx format

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
       RobotContainer.getInstance().setRed(alliance);
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
