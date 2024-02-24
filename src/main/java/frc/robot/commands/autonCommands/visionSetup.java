package frc.robot.commands.autonCommands;

import com.pathplanner.lib.path.PathPlannerTrajectory;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.driveCommands.setPoseCmd;
import frc.robot.commands.driveCommands.turnCmdSwerve;


public class visionSetup extends SequentialCommandGroup {

    public visionSetup() {
        String path1 = "C_To_CN";
        addCommands(new setPoseCmd(path1, 180));
        //addCommands(new turnCmdSwerve(50));

    }

}
