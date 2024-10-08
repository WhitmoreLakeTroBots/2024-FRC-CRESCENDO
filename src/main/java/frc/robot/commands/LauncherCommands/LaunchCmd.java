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
    private final double launchSenseDelay = 0.3;
    private final double launch_delayTime = 1.0;
    private LAUNCH_STEPS curr_step = LAUNCH_STEPS.PRE_LAUNCH_CHECKS;

    public LaunchCmd() {

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        RobotContainer.getInstance().m_Lighting.setNewBaseColor(lightPattern.RAINWAVES);
        bDone = false;
        curr_step = LAUNCH_STEPS.PRE_LAUNCH_CHECKS;
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

        switch (curr_step) {
            case PRE_LAUNCH_CHECKS:

                if ((RobotContainer.getInstance().m_Intake.isInPos(PivotPos.IN.getPos()) || (currTime >= pivot_endTime))
                        && RobotContainer.getInstance().m_Launcher.isReady()) {
                    RobotContainer.getInstance().m_Intake.setRollerStatus(RollerStatus.REVERSE);
                    launch_startTime = currTime;
                    launch_endTimeOut = launch_startTime + launch_delayTime;
                    launch_endTimeSense = launch_startTime + launchSenseDelay;
                    curr_step = LAUNCH_STEPS.LAUNCH;
                }
                break;

            case LAUNCH:
                if (currTime >= launch_endTimeOut) {
                    curr_step = LAUNCH_STEPS.POST_LAUNCH;
                }
                // sensor says note is gone... only delay a little bit.
                if ((RobotContainer.getInstance().m_Sensors.getBB1() == false) && (currTime > (launch_endTimeSense))) {
                    curr_step = LAUNCH_STEPS.POST_LAUNCH;
                }
                break;
            default:
                end(false);
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

    private enum LAUNCH_STEPS {
        PRE_LAUNCH_CHECKS,
        LAUNCH,
        POST_LAUNCH
    }
}
