package frc.robot.commands.driveCommands;

import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;

public class setPoseCmd extends Command {
    private boolean bDone = false;
    private String pathName = "";
    private double startingAngle = 0;
    public setPoseCmd(String npath, double startAngle) {
        pathName = npath;
        startingAngle = startAngle;
        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
    // if fixedDist = false => stagPosition is suposed to recieve the percantage to
    // be traversed in stag, in 0.xx format

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    PathPlannerPath path = PathPlannerPath.fromPathFile(pathName);

    var alliance = DriverStation.getAlliance();
    if (alliance.get() == DriverStation.Alliance.Red) {
      RobotContainer.getInstance().m_robotDrive.resetOdometry(new Pose2d(path.flipPath().getPathPoses().get(0).getX(), 
      path.flipPath().getPathPoses().get(0).getY(), new Rotation2d(Math.toRadians(startingAngle))));
     
    } else {
      RobotContainer.getInstance().m_robotDrive.resetOdometry(path.getPathPoses().get(0));
    
    }
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
