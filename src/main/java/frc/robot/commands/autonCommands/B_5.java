package frc.robot.commands.autonCommands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.LauncherCommands.AngleCmd;
import frc.robot.commands.LauncherCommands.LaunchCmd;
import frc.robot.commands.driveCommands.autoDriveCmd;
import frc.robot.commands.driveCommands.cmdResetGyro;
import frc.robot.commands.driveCommands.setPoseCmd;
import frc.robot.commands.driveCommands.turnCmd;
import frc.robot.commands.intakeCommands.intakeCmd;
import frc.robot.commands.intakeCommands.pivotCmd;
import frc.robot.subsystems.Intake.PivotPos;
import frc.robot.subsystems.Intake.RollerStatus;
import frc.robot.subsystems.Launcher.ANGLEPOS;
import frc.utils.cmdDelay;


public class B_5 extends SequentialCommandGroup {
    private final String path1 = "B_To_5";
    private final String path2 = "5_To_Shoot";

    public B_5() {
        addCommands(new cmdResetGyro().alongWith(new setPoseCmd(path1, 180)));
        addCommands(new AngleCmd(ANGLEPOS.TOPNOTEWING, true));
        addCommands(new turnCmd(-43, 0.3));
        addCommands(new cmdDelay(1));
        addCommands(new LaunchCmd());
        addCommands(new ParallelCommandGroup(
            new autoDriveCmd(path1),
            new intakeCmd(RollerStatus.FORWARD),
            new pivotCmd(PivotPos.OUT, true)
                ));
            addCommands(new ParallelCommandGroup(
            new autoDriveCmd(path2),
            new AngleCmd(ANGLEPOS.PODIUM, true),
            new intakeCmd(RollerStatus.STOP),
            new pivotCmd(PivotPos.IN, true)
                ));
        //addCommands(new AngleCmd(ANGLEPOS.CENTERNOTE, true));
        addCommands(new cmdDelay(1).andThen(new LaunchCmd()));
        addCommands(new cmdDelay(0).andThen(new AngleCmd(ANGLEPOS.START, true)));
    }

}
