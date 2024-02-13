package frc.robot.commands.autonCommands;

import edu.wpi.first.units.Angle;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.commands.cmdResetGyro;
import frc.robot.commands.LauncherCommands.AngleCmd;
import frc.robot.commands.LauncherCommands.LaunchCmd;
import frc.robot.commands.LauncherCommands.SetLauncherRPM;
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



public class speakerThreeNote extends SequentialCommandGroup {

    public speakerThreeNote() {

        final PathPlannerPath path1 = PathPlannerPath.fromPathFile("C_Speaker_To_CN");
        final PathPlannerPath path2 = PathPlannerPath.fromPathFile("CN_To_3");
        final PathPlannerPath path3 = PathPlannerPath.fromPathFile("3_To_CN");

        addCommands(new cmdResetGyro());
        addCommands(new AngleCmd(ANGLEPOS.UNDERSPEAKER, true));
        
        addCommands(new LaunchCmd());
        addCommands(new ParallelCommandGroup(
        new AutoBuilder().followPath(path1), 
        new intakeCmd(RollerStatus.FORWARD),
        new pivotCmd(PivotPos.OUT, true)));

        addCommands(new AngleCmd(ANGLEPOS.CENTERNOTE, true));
        addCommands(new LaunchCmd());
        addCommands(new AngleCmd(ANGLEPOS.START, true));
        addCommands(new ParallelCommandGroup(
            new AutoBuilder().followPath(path2),
            new SequentialCommandGroup(
                new cmdDelay(1.8),
                new intakeCmd(RollerStatus.FORWARD),
                new pivotCmd(PivotPos.OUT, true)))
            );
        addCommands(new ParallelCommandGroup(
            new AutoBuilder().followPath(path3),
            new SetLauncherRPM(3500),
            new SequentialCommandGroup(
            new cmdDelay(2.5)
            //,new AngleCmd(ANGLEPOS.CENTERNOTE, true)
            )));
        new AngleCmd(ANGLEPOS.CENTERNOTE, true);
        addCommands(new LaunchCmd());
    }

}
