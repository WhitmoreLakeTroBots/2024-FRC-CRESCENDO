package frc.utils;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Lighting.lightPattern;

public class cmdDelay extends Command {

    public boolean bdone = false;
    public double startTime = 0;
    public double endTime = 0;
    public double delayTime = 0;

    public cmdDelay(double seconds) {
        delayTime = seconds;
    }

    @Override
    public void initialize() {
        RobotContainer.getInstance().m_Lighting.setNewBaseColor(lightPattern.GOLD);
        bdone = false;
        startTime = RobotMath.getTime();
        endTime = startTime + delayTime;
        System.err.println("Delay for a bit");
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

        if (RobotMath.getTime() >= endTime) {
            bdone = true;
        }
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        bdone = true;
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return bdone;
    }

    @Override
    public boolean runsWhenDisabled() {
        return false;
    }
}