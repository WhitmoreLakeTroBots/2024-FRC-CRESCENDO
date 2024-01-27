package frc.robot.commands.LauncherCommands;

import edu.wpi.first.wpilibj2.command.Command;
//import java.util.function.DoubleSupplier;
import frc.robot.RobotContainer;

/**
 *
 */
public class SetLauncherRPM extends Command {

    private boolean bDone = false;
    private double newRPM = 0;

    public SetLauncherRPM(double nRPM) {
        newRPM = nRPM;

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    RobotContainer.getInstance().m_Launcher.setTargetRPM(newRPM);
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
