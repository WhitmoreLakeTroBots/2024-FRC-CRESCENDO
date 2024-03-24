package frc.robot.commands.autonCommands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.LauncherCommands.AngleCmd;
import frc.robot.commands.driveCommands.autoDriveCmd;
import frc.robot.commands.driveCommands.cmdDriveStraight;
import frc.robot.commands.driveCommands.cmdResetGyro;
import frc.robot.commands.driveCommands.setPoseCmd;
import frc.robot.commands.driveCommands.turnCmd;
import frc.robot.subsystems.Launcher.ANGLEPOS;
import frc.utils.cmdDelay;


public class driveStraight extends SequentialCommandGroup {
private final String Path1 = "Test1";
private final String Path2 = "Test2";
    public driveStraight() {
        addCommands(new cmdResetGyro());
        addCommands(new setPoseCmd(Path1, 180));
        addCommands(new autoDriveCmd(Path1));
        addCommands(new cmdDelay(2));
        addCommands(new autoDriveCmd(Path2));
    }

}
