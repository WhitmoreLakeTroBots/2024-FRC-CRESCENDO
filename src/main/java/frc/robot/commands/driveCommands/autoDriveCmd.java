package frc.robot.commands.driveCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Lighting.lightPattern;

//import java.util.function.DoubleSupplier;

/**
 *
 */
public class autoDriveCmd extends Command {
private boolean bDone = false;
private String npath;
private Command cdrive;

    public autoDriveCmd(String path) {
        npath = path;
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        RobotContainer.getInstance().m_Lighting.setNewBaseColor(lightPattern.RAINBOWLAVA);
        this.cdrive = RobotContainer.getInstance().m_robotDrive.followPathCommand(npath);
        this.cdrive.initialize();
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        this.cdrive.execute();
        bDone = this.cdrive.isFinished();
        if (bDone){
            end(false);
        }
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        this.cdrive.end(interrupted);
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
