package frc.robot.commands.driveCommands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import frc.utils.CommonLogic;
import frc.utils.RobotMath;
import frc.utils.SwerveUtils;

public class turnCmdSwerve extends Command {
    private boolean bDone = false;
    private double targetHeadingRAD = 0.0;
    private double stepSizeRAD = 0.0;
    private double headingTol = 10.0;
    private double minPow = -0.3;
    private double maxPow = 0.3;
    private double direction = 0.0;
    private double speed = 0.3;

    public turnCmdSwerve(double targetHeadingDEG, double speed, double stepSizeDEG) {
        targetHeadingRAD = Math.toRadians(targetHeadingDEG);
        stepSizeRAD = Math.toRadians(stepSizeDEG);
        minPow = -Math.abs(speed);
        maxPow = Math.abs(speed);
        direction = Math.signum(speed);
        // m_subsystem = subsystem; 
        // addRequirements(m_subsystem);

    }
     public turnCmdSwerve(double targetHeadingDEG, double speed) {
        targetHeadingRAD = Math.toRadians(gyroNormalize(targetHeadingDEG));
        stepSizeRAD = Math.toRadians(headingTol / 10) ;
        minPow = -Math.abs(speed);
        maxPow = Math.abs(speed);
        direction = Math.signum(speed);
        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
     public turnCmdSwerve(double targetHeadingDEG) {
        targetHeadingRAD = Math.toRadians(targetHeadingDEG);
        stepSizeRAD = Math.toRadians(headingTol / 10) ;
        direction = 1.0;
        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }
    // if fixedDist = false => stagPosition is suposed to recieve the percantage to
    // be traversed in stag, in 0.xx format

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        // RobotContainer.getInstance().m_robotDrive.drive(0, 0, speed, true, false);

        if(DriverStation.getAlliance().get() == DriverStation.Alliance.Red){
            targetHeadingRAD =  Math.toRadians(180) - targetHeadingRAD;
        } else {
            speed = speed *-1;
        }
       

        bDone = false;
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        double current = RobotContainer.getInstance().m_robotDrive.m_odometry.getEstimatedPosition().getRotation().getRadians();

        RobotContainer.getInstance().m_robotDrive.drive(0, 0,
                direction * Math.abs(CommonLogic.CapMotorPower(SwerveUtils.StepTowardsCircular(current, targetHeadingRAD, stepSizeRAD),
                 minPow, maxPow)), true, false);

        String msg = String.format ("Current: %.4f Target: %.4f",
        current, targetHeadingRAD);
        System.err.println(msg);

        if (gyroInTol(Math.toDegrees(current), Math.toDegrees(targetHeadingRAD), headingTol)) {
            RobotContainer.getInstance().m_robotDrive.stopDrive();
            bDone = true;
            end(false);
        }
        ;
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
    private boolean isInRange(double currentPos, double targetPos, double tol) {
        boolean bIsInRange = false;
        if ((Math.abs(currentPos) >= Math.abs(targetPos - tol)) && (Math.abs(currentPos) <= Math.abs(targetPos + tol))) {
            bIsInRange = true;
        }

        return bIsInRange;
    }
    private double gyroNormalize(double heading) {
        // takes the full turns out of heading
        // gives us values from 0 to 180 for the right side of the robot
        // and values from 0 to -179 degrees for the left side of the robot
        double degrees = heading % 360;
    
        if (degrees > 180) {
          degrees = degrees - 360;
        }
        if (degrees < -179) {
          degrees = degrees + 360;
        }
        return degrees;
      }
      public boolean gyroInTol(double currHeading, double desiredHeading, double tol) {

        double upperTol = gyroNormalize(desiredHeading + tol);
        double lowerTol = gyroNormalize(desiredHeading - tol);
        double normalCurr = gyroNormalize(currHeading);
        double signumUpperTol = Math.signum(upperTol);
        double signumLowerTol = Math.signum(lowerTol);
        boolean retValue = false;
        // works for all positive numbers direction values
        if (signumUpperTol > 0 && signumLowerTol > 0) {
          if ((normalCurr >= lowerTol) && (normalCurr <= upperTol)) {
            retValue = true;
          }
        }
        // works for negative values
        else if (signumUpperTol < 0 && signumLowerTol < 0) {
          if ((normalCurr >= lowerTol) && (normalCurr <= upperTol)) {
            retValue = true;
          }
        }
        // mixed values -tol to + tol This happens at 180 degrees
        else if ((signumUpperTol < 0) && (signumLowerTol > 0)) {
          if ((Math.abs(normalCurr) >= Math.abs(lowerTol)) && (Math.abs(normalCurr) >= Math.abs(upperTol))) {
            retValue = true;
          }
        }
        // mixed values -tol to + tol This happens at 0 degrees
        else if ((signumUpperTol > 0) && (signumLowerTol < 0)) {
          if ((Math.abs(normalCurr) <= Math.abs(lowerTol)) && (Math.abs(normalCurr) <= Math.abs(upperTol))) {
            retValue = true;
          }
        }
        return (retValue);
      }

}
