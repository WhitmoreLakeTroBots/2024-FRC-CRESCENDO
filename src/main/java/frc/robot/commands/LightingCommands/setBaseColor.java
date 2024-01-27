package frc.robot.commands.LightingCommands;

import edu.wpi.first.wpilibj2.command.Command;
//import java.util.function.DoubleSupplier;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Lighting;
import frc.robot.subsystems.Lighting.lightPattern;

/**
 *
 */
public class setBaseColor extends Command {

    private boolean bDone = false;
    private Lighting.lightPattern newLPattern = lightPattern.BLACK;

    public setBaseColor(Lighting.lightPattern nLightPattern) {
        newLPattern = nLightPattern;

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    RobotContainer.getInstance().m_Lighting.setNewBaseColor(newLPattern.getValue());
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
