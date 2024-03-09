package frc.robot.commands.autonCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.LauncherCommands.AngleCmd;
import frc.robot.commands.LauncherCommands.LaunchCmd;
import frc.robot.commands.LauncherCommands.SetLauncherRPM;
import frc.robot.commands.driveCommands.autoDriveCmd;
import frc.robot.commands.driveCommands.cmdResetGyro;
import frc.robot.commands.driveCommands.setPoseCmd;
import frc.robot.commands.intakeCommands.intakeCmd;
import frc.robot.commands.intakeCommands.pivotCmd;
import frc.robot.subsystems.Intake.PivotPos;
import frc.robot.subsystems.Intake.RollerStatus;
import frc.robot.subsystems.Launcher.ANGLEPOS;
import frc.utils.cmdDelay;

public class centerFourNote extends Command {
    private boolean bDone = false;
    final String path1 = "C_To_CN";
    final String path2 = "CN_To_3";
    final String path3 = "3_To_CN";

    private enum Step {
        PRESTART,
        NOTEONE,
        GETNOTETWO,
        LAUNCHNOTETWO,
        GETNOTETHREE,
        LAUNCHNOTETHREE,
        GETNOTEFOUR,
        GETNOTEFOURDIRECT,
        LAUNCHNOTEFOUR;
    }

    private Step cStep = Step.PRESTART;

    public centerFourNote() {

        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
    // if fixedDist = false => stagPosition is suposed to recieve the percantage to
    // be traversed in stag, in 0.xx format

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {

        bDone = true;
        end(bDone);
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        switch (cStep) {
            case PRESTART:
                new SequentialCommandGroup(
                        new cmdResetGyro().alongWith(new setPoseCmd(path1, 180)));
                cStep = Step.NOTEONE;
                break;
            case NOTEONE:
                new SequentialCommandGroup(
                        new cmdResetGyro().alongWith(new setPoseCmd(path1, 180)),
                        new AngleCmd(ANGLEPOS.UNDERSPEAKER, true),
                        new cmdDelay(1),
                        new LaunchCmd());

                // start note 3

                
                    // drive back
                   
                break;
            case GETNOTETWO:
                new SequentialCommandGroup(
                        new ParallelCommandGroup(
                                new autoDriveCmd(path1),
                                new AngleCmd(ANGLEPOS.CENTERNOTE, false), new intakeCmd(RollerStatus.FORWARD),
                                new pivotCmd(PivotPos.OUT, true)),
                        new cmdDelay(1).andThen(new LaunchCmd()));
                cStep = Step.GETNOTETHREE;
                break;
            case GETNOTETHREE:
                new SequentialCommandGroup(
                        new AngleCmd(ANGLEPOS.START, true),
                        new cmdDelay(0.5).andThen(
                                new ParallelCommandGroup(
                                        new autoDriveCmd(path2),
                                        new SequentialCommandGroup(
                                                new cmdDelay(1.8),
                                                new intakeCmd(RollerStatus.FORWARD),
                                                new pivotCmd(PivotPos.OUT, true)))));
            if (RobotContainer.getInstance().m_Sensors.getBB1()) {
                cStep = Step.LAUNCHNOTETHREE;
            }else {
                cStep = Step.GETNOTEFOUR;
            }
                break;
            case LAUNCHNOTETHREE:
                 new SequentialCommandGroup(
                            new ParallelCommandGroup(
                                    new autoDriveCmd(path3),
                                    new pivotCmd(PivotPos.IN, false),
                                    new intakeCmd(RollerStatus.STOP),
                                    new SetLauncherRPM(3500),
                                    new SequentialCommandGroup(
                                            new cmdDelay(2))),
                            new AngleCmd(ANGLEPOS.CENTERNOTE, true),
                            new cmdDelay(1),
                            new LaunchCmd());
                            cStep = Step.GETNOTEFOUR;
                break;
            case GETNOTEFOUR:
            break;
            case GETNOTEFOURDIRECT:
            break;
            case LAUNCHNOTEFOUR:
            break;
            default:
                break;
        }

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
