package frc.robot.commands.autonCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
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
import frc.robot.subsystems.Launcher;
import frc.robot.subsystems.Intake.PivotPos;
import frc.robot.subsystems.Intake.RollerStatus;
import frc.robot.subsystems.Launcher.ANGLEPOS;
import frc.utils.cmdDelay;

public class centerFourNote extends Command {
    private boolean bDone = false;
    final String _CToCN = "C_To_CN";
    final String _CNTo3 = "CN_To_3";
    final String _3ToCn = "3_To_CN";
    final String _3To2 = "3_To_2";
    final String _2ToShoot = "2_To_Shoot";
    final String _CNTo2 = "CN_To_2";

    private enum Step {
        PRESTART,
        NOTEONE,
        GETNOTETWO,
        LAUNCHNOTETWO,
        GETNOTETHREE,
        LAUNCHNOTETHREE,
        GETNOTEFOUR,
        GETNOTEFOURDIRECT,
        LAUNCHNOTEFOUR,
        SHUTDOWN;
    }

    private SequentialCommandGroup preStartCmd = null;
    private SequentialCommandGroup noteOneCmd = null;
    private SequentialCommandGroup getNoteTwoCmd = null;
    private SequentialCommandGroup getNoteThreeCmd = null;
    private SequentialCommandGroup launchNoteThreeCmd = null;
    private SequentialCommandGroup getNoteFourCmd = null;
    private SequentialCommandGroup getNoteFourDirectCmd = null;
    private SequentialCommandGroup launchNoteFourCmd = null;
    private SequentialCommandGroup shutdownCmd = null;

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

    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

        switch (cStep) {
            case PRESTART:
                if (preStartCmd == null) {
                    preStartCmd = new SequentialCommandGroup();
                    preStartCmd.addCommands(new cmdResetGyro().alongWith(new setPoseCmd(_CToCN, 180)));
                    preStartCmd.initialize();
                } else {
                    preStartCmd.execute();
                }

                if (preStartCmd.isFinished()) {
                    preStartCmd.end(false);
                    cStep = Step.NOTEONE;
                }
                break;

            case NOTEONE:
                if (noteOneCmd == null) {
                    noteOneCmd = new SequentialCommandGroup(
                            new AngleCmd(ANGLEPOS.UNDERSPEAKER, true),
                            new cmdDelay(0),
                            new LaunchCmd());
                    noteOneCmd.initialize();
                } else {
                    noteOneCmd.execute();
                }
                if (noteOneCmd.isFinished()) {
                    noteOneCmd.end(false);
                    cStep = Step.GETNOTETWO;
                }

                // start note 3

                // drive back

                break;
            case GETNOTETWO:
                if (getNoteTwoCmd == null) {
                    getNoteTwoCmd = new SequentialCommandGroup(
                            new ParallelCommandGroup(
                                    new autoDriveCmd(_CToCN),
                                    new AngleCmd(ANGLEPOS.CENTERNOTE, false), new intakeCmd(RollerStatus.FORWARD),
                                    new pivotCmd(PivotPos.OUT, true)),
                            new cmdDelay(0).andThen(new LaunchCmd()));
                    getNoteTwoCmd.initialize();
                } else {
                    getNoteTwoCmd.execute();
                }
                if (getNoteTwoCmd.isFinished()) {
                    getNoteTwoCmd.end(false);
                    cStep = Step.GETNOTETHREE;
                }
                break;
            case GETNOTETHREE:
                if (getNoteThreeCmd == null) {
                    getNoteThreeCmd = new SequentialCommandGroup(
                            new AngleCmd(ANGLEPOS.START, true),
                            new cmdDelay(0.5).andThen(
                                    new ParallelCommandGroup(
                                            new autoDriveCmd(_CNTo3),
                                            new SequentialCommandGroup(
                                                    new cmdDelay(1.8),
                                                    new intakeCmd(RollerStatus.FORWARD),
                                                    new pivotCmd(PivotPos.OUT, true)))));
                    getNoteThreeCmd.initialize();
                } else {
                    getNoteThreeCmd.execute();
                }
                if (getNoteThreeCmd.isFinished()) {
                    getNoteThreeCmd.end(false);
                    if (RobotContainer.getInstance().m_Sensors.getBB1()) {
                        cStep = Step.LAUNCHNOTETHREE;
                    } else {
                        cStep = Step.GETNOTEFOUR;
                    }
                }

                break;
            case LAUNCHNOTETHREE:
                if (launchNoteThreeCmd == null) {
                    launchNoteThreeCmd = new SequentialCommandGroup(
                            new ParallelCommandGroup(
                                    new autoDriveCmd(_3ToCn),
                                    new pivotCmd(PivotPos.IN, false),
                                    new intakeCmd(RollerStatus.STOP),
                                    new SetLauncherRPM(3500),
                                    new SequentialCommandGroup(
                                            new cmdDelay(2))),
                            new AngleCmd(ANGLEPOS.CENTERNOTE, true),
                            new cmdDelay(0),
                            new LaunchCmd());
                    launchNoteThreeCmd.initialize();
                }

                if (launchNoteThreeCmd.isFinished()) {
                    launchNoteThreeCmd.end(false);
                    cStep = Step.GETNOTEFOUR;
                }
                break;

            case GETNOTEFOURDIRECT:
                if (getNoteFourDirectCmd == null) {
                    getNoteFourDirectCmd = new SequentialCommandGroup(
                            new AngleCmd(ANGLEPOS.START, true),
                            new ParallelCommandGroup(
                                    new autoDriveCmd(_3To2),
                                    new SequentialCommandGroup(
                                            new cmdDelay(1.8),
                                            new intakeCmd(RollerStatus.FORWARD),
                                            new pivotCmd(PivotPos.OUT, true))));
                    getNoteFourDirectCmd.initialize();
                }
                if (getNoteFourDirectCmd.isFinished()) {
                    getNoteFourDirectCmd.end(false);
                    cStep = Step.LAUNCHNOTEFOUR;
                }
                break;

            case GETNOTEFOUR:
                if (getNoteFourCmd == null) {
                    getNoteFourCmd = new SequentialCommandGroup(
                            new AngleCmd(ANGLEPOS.START, true),
                            new ParallelCommandGroup(
                                    new autoDriveCmd(_CNTo2),
                                    new SequentialCommandGroup(
                                            new cmdDelay(1.8),
                                            new intakeCmd(RollerStatus.FORWARD),
                                            new pivotCmd(PivotPos.OUT, true))));
                    getNoteFourCmd.initialize();
                }

                if (getNoteFourCmd.isFinished()) {
                    getNoteFourCmd.end(false);
                    cStep = Step.LAUNCHNOTEFOUR;
                }
                break;

            case LAUNCHNOTEFOUR:
                if (launchNoteFourCmd == null) {
                    launchNoteFourCmd = new SequentialCommandGroup(
                            new ParallelCommandGroup(
                                    new autoDriveCmd(_2ToShoot),
                                    new pivotCmd(PivotPos.IN, false),
                                    new intakeCmd(RollerStatus.STOP),
                                    new AngleCmd(ANGLEPOS.CENTERNOTE, true)),
                            new LaunchCmd());
                } else {
                    launchNoteFourCmd.execute();
                }
                if (launchNoteFourCmd.isFinished())
                    launchNoteFourCmd.end(false);
                cStep = Step.SHUTDOWN;
                break;

            case SHUTDOWN:
                if (shutdownCmd == null) {
                    shutdownCmd = new SequentialCommandGroup(
                            new pivotCmd(PivotPos.IN, false),
                            new intakeCmd(RollerStatus.STOP),
                            new SetLauncherRPM(0.0));
                    shutdownCmd.initialize();
                }
                // TODO -- Stop things and brace for impact of another bot crashing into us.
                if (shutdownCmd.isFinished()) {
                    shutdownCmd.end(false);
                    bDone = true;
                }
                break;

            default:
                cStep = Step.SHUTDOWN;
                break;
        }
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        bDone = true;
        // TODO    Look at the value of cStep and call end on the currently running command group.

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