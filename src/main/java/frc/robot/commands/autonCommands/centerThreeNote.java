package frc.robot.commands.autonCommands;

import edu.wpi.first.units.Angle;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.commands.LauncherCommands.AngleCmd;
import frc.robot.commands.LauncherCommands.LaunchCmd;
import frc.robot.commands.LauncherCommands.SetLauncherRPM;
import frc.robot.commands.driveCommands.autoDriveCmd;
import frc.robot.commands.driveCommands.cmdResetGyro;
import frc.robot.commands.driveCommands.setPoseCmd;
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



public class centerThreeNote extends SequentialCommandGroup {

    public centerThreeNote() {

        final String path1 = "C_To_CN";
        final String path2 = "CN_To_3";
        final String path3 = "3_To_CN";

        addCommands(new cmdResetGyro().alongWith(new setPoseCmd(path1, 180)));
        addCommands(new AngleCmd(ANGLEPOS.UNDERSPEAKER, true));
        addCommands(new cmdDelay(1));
        addCommands(new LaunchCmd());
        addCommands(new ParallelCommandGroup(
            new autoDriveCmd(path1),
            new AngleCmd(ANGLEPOS.CENTERNOTE, false)
            ,new intakeCmd(RollerStatus.FORWARD),
            new pivotCmd(PivotPos.OUT, true)
                ));
        //addCommands(new AngleCmd(ANGLEPOS.CENTERNOTE, true));
        addCommands(new cmdDelay(1).andThen(new LaunchCmd()));
        //addCommands(new cmdDelay(0).andThen(new AngleCmd(ANGLEPOS.START, true)));

        // start note 3
        addCommands(new AngleCmd(ANGLEPOS.START, true));
        addCommands(new cmdDelay(0.5).andThen(
            new ParallelCommandGroup(
                new autoDriveCmd(path2),
                new SequentialCommandGroup(
                    new cmdDelay(1.8),
                    new intakeCmd(RollerStatus.FORWARD),
                    new pivotCmd(PivotPos.OUT, true)))
            ));

            // drive back
        addCommands(new ParallelCommandGroup(
            new autoDriveCmd(path3),
            new pivotCmd(PivotPos.IN, false),
            new intakeCmd(RollerStatus.STOP),
            new SetLauncherRPM(3500),
            new SequentialCommandGroup(
            new cmdDelay(2)
            //,new AngleCmd(ANGLEPOS.CENTERNOTE, true)
            )));
        addCommands(new AngleCmd(ANGLEPOS.CENTERNOTE, true));
        addCommands(new cmdDelay(1));
        addCommands(new LaunchCmd());
    }

}
