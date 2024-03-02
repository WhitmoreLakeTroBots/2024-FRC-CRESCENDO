package frc.robot.commands.autonCommands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.LauncherCommands.AngleCmd;
import frc.robot.commands.driveCommands.cmdDriveStraight;
import frc.robot.commands.driveCommands.cmdResetGyro;
import frc.robot.subsystems.Launcher.ANGLEPOS;


public class driveStraight extends SequentialCommandGroup {

    public driveStraight() {
        addCommands(new cmdResetGyro());
        //addCommands(new AngleCmd(ANGLEPOS.UNDERSPEAKER, true));
        addCommands(new cmdDriveStraight(40, 0.4, 0));
    }

}
