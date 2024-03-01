package frc.robot.commands.driveCommands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.utils.CommonLogic;
import frc.utils.RobotMath;

public class turnCmd extends Command {
    private boolean bDone = false;
    private double heading = 0.0;
    private double speed = 0.0;
    private double headingTol = 5.0;
    private double pivP = 0.008;
    private double pivF = 0.0;
    public turnCmd(double targetHeading, double rotSpeed) {
        heading = targetHeading;
        speed = rotSpeed;
        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
    // if fixedDist = false => stagPosition is suposed to recieve the percantage to
    // be traversed in stag, in 0.xx format

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
       //RobotContainer.getInstance().m_robotDrive.drive(0, 0, speed, true, false);
       
        bDone = false;
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        RobotContainer.getInstance().m_robotDrive.drive(0,0,
           CommonLogic.CapMotorPower(
                CommonLogic.gotoPosPIDF(pivP,pivF,-RobotContainer.getInstance().m_robotDrive.m_gyro.getNormaliziedNavxAngle(),heading) 
                ,-speed,speed)
        ,false,false);

        if (RobotMath.isInRange(-RobotContainer.getInstance().m_robotDrive.m_gyro.getNormaliziedNavxAngle(), heading, headingTol)){
            RobotContainer.getInstance().m_robotDrive.stopDrive();
        bDone = true;
        end(false);
        };
        String msg = String.format ("Target: %.4f Current: %.4f",
        heading, -RobotContainer.getInstance().m_robotDrive.m_gyro.getNormaliziedNavxAngle());
        System.err.println(msg);
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
