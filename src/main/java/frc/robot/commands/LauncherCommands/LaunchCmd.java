package frc.robot.commands.LauncherCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Intake.RollerStatus;
import frc.robot.subsystems.Intake.PivotPos;
import frc.robot.subsystems.Lighting.lightPattern;
import frc.utils.CommonLogic;
import frc.utils.RobotMath;

/**
 *
 */
public class LaunchCmd extends Command {
    private boolean bDone = false;
    private double currTime = 0;

    private double pivot_delayTime = 1.0;
    private double pivot_startTime = 0;
    private double pivot_endTime = 0;

    private double launch_startTime = 0;
    private double launch_endTimeOut = 0;
    private double launch_endTimeSense = 0;
    private final double launchSenseDelay = 0.4;
    private final double launch_delayTime = 1.0;

    public LaunchCmd() {

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        RobotContainer.getInstance().m_Lighting.setNewBaseColor(lightPattern.RAINWAVES);
        bDone = false;
        currTime = RobotMath.getTime();
        pivot_startTime = currTime;
        pivot_endTime = pivot_startTime + pivot_delayTime;

        launch_startTime = pivot_endTime;
        launch_endTimeOut = launch_startTime + launch_delayTime;
        launch_endTimeSense = launch_startTime + launchSenseDelay;

    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

        currTime = RobotMath.getTime();
        if ((RobotContainer.getInstance().m_Intake.isInPos(PivotPos.IN.getPos()) || (currTime >= pivot_endTime)) &&
                (RobotContainer.getInstance().m_Intake.getCurRollerStatus() != RollerStatus.REVERSE)
                && (CommonLogic.isInRange(RobotContainer.getInstance().m_Launcher.getActualRPM(),
                        RobotContainer.getInstance().m_Launcher.getTargetRPM(), 250)
                        && CommonLogic.isInRange(RobotContainer.getInstance().m_Launcher.getAnglePosActual(),
                                RobotContainer.getInstance().m_Launcher.getAnglePos().getpos(), 2))) {
            RobotContainer.getInstance().m_Intake.setRollerStatus(RollerStatus.REVERSE);
            launch_startTime = currTime;
            launch_endTimeOut = launch_startTime + launch_delayTime;
            launch_endTimeSense = launch_startTime + launchSenseDelay;
        }

        // timeout end
        if (currTime >= launch_endTimeOut) {
            bDone = true;
            this.end(false);
        }

        // sensor says note is gone... only delay a little bit.
        if ((RobotContainer.getInstance().m_Sensors.getBB1() == false) && (currTime > (launch_endTimeSense))) {
            bDone = true;
            this.end(false);
        }
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        bDone = true;
        RobotContainer.getInstance().m_Intake.setRollerStatus(RollerStatus.STOP);
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
