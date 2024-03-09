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
    final String _CToCN = "C_To_CN";
    final String _CNTo3 = "CN_To_3";
    final String _3ToCn = "3_To_CN";
    final String _3To2 = "3_To_2";
    final String _2ToShoot = "2_To_Shoot";
    final String _CNTo2 = "CN_To_2";

    public centerFourNote() {

        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
    // if fixedDist = false => stagPosition is suposed to recieve the percantage to
    // be traversed in stag, in 0.xx format

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
 System.err.println("starting");
        new SequentialCommandGroup(
                new cmdResetGyro().alongWith(new setPoseCmd(_CToCN, 180)));

        // note one
        System.err.println("note one");
        new SequentialCommandGroup(
                new AngleCmd(ANGLEPOS.UNDERSPEAKER, true),
                new cmdDelay(1),
                new LaunchCmd());

        // note two
        new SequentialCommandGroup(
                new ParallelCommandGroup(
                        new autoDriveCmd(_CToCN),
                        new AngleCmd(ANGLEPOS.CENTERNOTE, false), new intakeCmd(RollerStatus.FORWARD),
                        new pivotCmd(PivotPos.OUT, true)),
                new cmdDelay(1).andThen(new LaunchCmd()));

        // note three/
        new SequentialCommandGroup(
                new AngleCmd(ANGLEPOS.START, true),
                new cmdDelay(0.5).andThen(
                        new ParallelCommandGroup(
                                new autoDriveCmd(_CNTo3),
                                new SequentialCommandGroup(
                                        new cmdDelay(1.8),
                                        new intakeCmd(RollerStatus.FORWARD),
                                        new pivotCmd(PivotPos.OUT, true)))));
        if (RobotContainer.getInstance().m_Sensors.getBB1()) {
            // launch note three
            new SequentialCommandGroup(
                    new ParallelCommandGroup(
                            new autoDriveCmd(_3ToCn),
                            new pivotCmd(PivotPos.IN, false),
                            new intakeCmd(RollerStatus.STOP),
                            new SetLauncherRPM(3500),
                            new SequentialCommandGroup(
                                    new cmdDelay(2))),
                    new AngleCmd(ANGLEPOS.CENTERNOTE, true),
                    new cmdDelay(1),
                    new LaunchCmd());
            // get note four from launch
            new SequentialCommandGroup(
                    new AngleCmd(ANGLEPOS.START, true),
                    new ParallelCommandGroup(
                            new autoDriveCmd(_CNTo2),
                            new SequentialCommandGroup(
                                    new cmdDelay(1.8),
                                    new intakeCmd(RollerStatus.FORWARD),
                                    new pivotCmd(PivotPos.OUT, true))));
            // launch note four
            new SequentialCommandGroup(
                    new ParallelCommandGroup(
                            new autoDriveCmd(_2ToShoot),
                            new pivotCmd(PivotPos.IN, false),
                            new intakeCmd(RollerStatus.STOP),
                            new SequentialCommandGroup(
                                    new AngleCmd(ANGLEPOS.CENTERNOTE, true),
                                    new cmdDelay(2.5),
                                    new LaunchCmd())));
        } else /* get note four direct */ {
            new SequentialCommandGroup(
                    new AngleCmd(ANGLEPOS.START, true),
                    new ParallelCommandGroup(
                            new autoDriveCmd(_3To2),
                            new SequentialCommandGroup(
                                    new cmdDelay(1.8),
                                    new intakeCmd(RollerStatus.FORWARD),
                                    new pivotCmd(PivotPos.OUT, true))));
            // launch note four
            new SequentialCommandGroup(
                    new ParallelCommandGroup(
                            new autoDriveCmd(_2ToShoot),
                            new pivotCmd(PivotPos.IN, false),
                            new intakeCmd(RollerStatus.STOP),
                            new SequentialCommandGroup(
                                    new AngleCmd(ANGLEPOS.CENTERNOTE, true),
                                    new cmdDelay(2.5),
                                    new LaunchCmd())));
        }
        
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
       
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
