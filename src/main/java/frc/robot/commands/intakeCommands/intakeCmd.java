package frc.robot.commands.intakeCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Intake;

//import java.util.function.DoubleSupplier;

/**
 *
 */
public class intakeCmd extends Command {
private boolean bDone = false;
private Intake.RollerStatus setStatus;

    public intakeCmd(Intake.RollerStatus nStatus) {
        setStatus = nStatus;
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        RobotContainer.getInstance().m_Intake.setRollerStatus(setStatus);
        bDone = true;
        end(false);
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
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
