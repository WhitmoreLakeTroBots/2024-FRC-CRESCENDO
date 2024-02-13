package frc.robot.commands.autonCommands;

import edu.wpi.first.units.Angle;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.commands.cmdResetGyro;
import frc.robot.commands.LauncherCommands.AngleCmd;
import frc.robot.commands.LauncherCommands.LaunchCmd;
import frc.robot.commands.intakeCommands.intakeCmd;
import frc.robot.commands.intakeCommands.pivotCmd;
import frc.robot.subsystems.Gyro;
import frc.robot.subsystems.Launcher;
import frc.robot.subsystems.Intake.PivotPos;
import frc.robot.subsystems.Intake.RollerStatus;
import frc.robot.subsystems.Launcher.ANGLEPOS;
import frc.utils.cmdDelay;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.*;
import com.pathplanner.lib.path.PathPlannerPath;



public class speakerTwoNote extends SequentialCommandGroup {

    public speakerTwoNote() {

        final PathPlannerPath path1 = PathPlannerPath.fromPathFile("C_Speaker_To_CN");


        addCommands(new cmdResetGyro());
        addCommands(new AngleCmd(ANGLEPOS.UNDERSPEAKER, true));
        addCommands(new cmdDelay(0));
        addCommands(new LaunchCmd());
        addCommands(new ParallelCommandGroup(
            new  AutoBuilder().followPath(path1),
            new intakeCmd(RollerStatus.FORWARD),
            new pivotCmd(PivotPos.OUT, true)));
        addCommands(new AngleCmd(ANGLEPOS.CENTERNOTE, true));
        addCommands(new LaunchCmd());
    }

}
