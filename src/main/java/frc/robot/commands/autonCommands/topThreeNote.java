package frc.robot.commands.autonCommands;

import edu.wpi.first.units.Angle;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.commands.LauncherCommands.AngleCmd;
import frc.robot.commands.LauncherCommands.LaunchCmd;
import frc.robot.commands.driveCommands.autoDriveCmd;
import frc.robot.commands.driveCommands.cmdResetGyro;
import frc.robot.commands.driveCommands.setPoseCmd;
import frc.robot.commands.driveCommands.turnCmd;
import frc.robot.commands.driveCommands.turnCmdSwerve;
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
import com.pathplanner.lib.controllers.PathFollowingController;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.util.PathPlannerLogging;

public class topThreeNote extends SequentialCommandGroup {

    public topThreeNote() {

        final String path1 = "T_To_TN";
        final String path2 = "TN_To_1";
        final String path3 = "1_To_Shoot";

        addCommands(new cmdResetGyro().alongWith(new setPoseCmd(path1, 180)));
        addCommands(new AngleCmd(ANGLEPOS.TOPNOTEWING, true));
        addCommands(new turnCmdSwerve(35, -0.2));
        addCommands(new cmdDelay(1));
        addCommands(new LaunchCmd());
        addCommands(new ParallelCommandGroup(
                new autoDriveCmd(path1),
                new AngleCmd(ANGLEPOS.PODIUM, false), new intakeCmd(RollerStatus.FORWARD),
                new pivotCmd(PivotPos.OUT, true)));

        addCommands(new turnCmdSwerve(30, -0.2));
        addCommands(new cmdDelay(1).andThen(new LaunchCmd()));
        addCommands(new cmdDelay(0).andThen(new AngleCmd(ANGLEPOS.START, true)));
        addCommands(new ParallelCommandGroup(
                new autoDriveCmd(path2),
                new intakeCmd(RollerStatus.FORWARD),
                new pivotCmd(PivotPos.OUT, true)));
        addCommands(new ParallelCommandGroup(
                new autoDriveCmd(path3),
                new AngleCmd(ANGLEPOS.TOPLAUNCH, false), new intakeCmd(RollerStatus.FORWARD),
                new pivotCmd(PivotPos.OUT, true)));
        addCommands(new cmdDelay(1).andThen(new LaunchCmd()));

    }

}
