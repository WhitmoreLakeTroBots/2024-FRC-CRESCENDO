package frc.robot.commands.LauncherCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Launcher;

//import java.util.function.DoubleSupplier;

/**
 *
 */
public class AngleCmd extends Command {
    private boolean bDone = false;
    private boolean bWait = false;
    private Launcher.ANGLEPOS nAnglePos;

    public AngleCmd(Launcher.ANGLEPOS newAnglePos, boolean Wait) {
        nAnglePos = newAnglePos;
        bWait = Wait;

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        RobotContainer.getInstance().m_Launcher.setAnglePos(nAnglePos);
        if(!bWait){
            bDone = true;
            end(false);
        }
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        if(RobotContainer.getInstance().m_Launcher.getAngleStatus()){
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
        return bDone;
    }

    @Override
    public boolean runsWhenDisabled() {
        return false;

    }
}
