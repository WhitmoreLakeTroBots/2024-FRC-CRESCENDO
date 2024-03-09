package frc.robot.commands.driveCommands;

import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import edu.wpi.first.math.geometry.Pose3d;
public class setVisionPoseCmd extends Command {
    private boolean bDone;
    public setVisionPoseCmd() {

        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
    // if fixedDist = false => stagPosition is suposed to recieve the percantage to
    // be traversed in stag, in 0.xx format

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
         Pose3d pose = RobotContainer.getInstance().m_CameraHelper.getAveragePose3d();

      RobotContainer.getInstance().m_robotDrive.resetOdometry(new Pose2d(pose.getX(),pose.getY(), pose.getRotation().toRotation2d()));
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
