package frc.robot.commands.LauncherCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Launcher;
import frc.robot.subsystems.Intake.RollerStatus;
import frc.utils.RobotMath;

//import java.util.function.DoubleSupplier;

/**
 *
 */
public class LaunchCmd extends Command {
    private boolean bDone = false;
    public double startTime = 0;
    public double endTime = 0;
    public double delayTime = 1;

    public LaunchCmd() {

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        bDone = false;
        startTime = RobotMath.getTime();
        endTime = startTime + delayTime;
        System.err.println("Delay for a bit");
        RobotContainer.getInstance().m_Intake.setRollerStatus(RollerStatus.REVERSE);
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        if (RobotMath.getTime() >= endTime) {
            bDone = true;
        RobotContainer.getInstance().m_Intake.setRollerStatus(RollerStatus.STOP);

        }
      /*  if (RobotContainer.getInstance().m_Sensors.getBB1() == false) {
            bDone = true; 
        }*/
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
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
