package frc.robot.commands.autonCommands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.LauncherCommands.AngleCmd;
import frc.robot.commands.LauncherCommands.LaunchCmd;
import frc.robot.commands.driveCommands.cmdResetGyro;
import frc.robot.commands.driveCommands.turnCmd;
import frc.robot.subsystems.Launcher.ANGLEPOS;


public class launchAuton extends SequentialCommandGroup {

    public launchAuton() {
addCommands(new AngleCmd(ANGLEPOS.UNDERSPEAKER, true));
addCommands(new LaunchCmd());
    }
    

}
