package frc.robot.commands.driveCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.utils.RobotMath;

public class turnCmd extends Command {
    private boolean bDone = false;
    private double heading = 0.0;
    private double speed = 0.0;
    private double headingTol = 3.0;
    public turnCmd(double targetHeading, double rotSpeed) {
        heading = targetHeading;
        speed = rotSpeed;
        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
    // if fixedDist = false => stagPosition is suposed to recieve the percantage to
    // be traversed in stag, in 0.xx format

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
       RobotContainer.getInstance().m_robotDrive.drive(0, 0, speed, true, false);
       
        bDone = false;
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        if (RobotMath.isInRange(RobotContainer.getInstance().m_robotDrive.m_gyro.getNormaliziedNavxAngle(), heading, headingTol)){
        bDone = true;
        };
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
