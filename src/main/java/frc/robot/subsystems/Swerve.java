package frc.robot.subsystems;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.Constants.CANIDs;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.RobotContainer;
//import frc.robot.commands.*;

// import java.util.List;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.FollowPathHolonomic;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.WPIUtilJNI;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import frc.utils.*;

/**
 *
 */
public class Swerve extends SubsystemBase {
  public final double kp_driveStraightGyro = 0.0125;
  private final double maxTurnPow = 0.3;
  private final double minTurnPow = -0.3;

  // Create MAXSwerveModules
  private final MAXSwerveModule m_frontLeft = new MAXSwerveModule(
      CANIDs.kFrontLeftDrivingCanId,
      CANIDs.kFrontLeftTurningCanId,
      DriveConstants.kFrontLeftChassisAngularOffset);

  private final MAXSwerveModule m_frontRight = new MAXSwerveModule(
      CANIDs.kFrontRightDrivingCanId,
      CANIDs.kFrontRightTurningCanId,
      DriveConstants.kFrontRightChassisAngularOffset);

  private final MAXSwerveModule m_rearLeft = new MAXSwerveModule(
      CANIDs.kRearLeftDrivingCanId,
      CANIDs.kRearLeftTurningCanId,
      DriveConstants.kBackLeftChassisAngularOffset);

  private final MAXSwerveModule m_rearRight = new MAXSwerveModule(
      CANIDs.kRearRightDrivingCanId,
      CANIDs.kRearRightTurningCanId,
      DriveConstants.kBackRightChassisAngularOffset);

  // private List<MAXSwerveModule> m_swerveModules;

  // moved to a subsystem with all of the WLRobotics functions
  public final Gyro m_gyro = new Gyro();

  // Slew rate filter variables for controlling lateral acceleration
  private double m_currentRotation = 0.0;
  private double m_currentTranslationDir = 0.0;
  private double m_currentTranslationMag = 0.0;

  private SlewRateLimiter m_magLimiter = new SlewRateLimiter(DriveConstants.kMagnitudeSlewRate);
  private SlewRateLimiter m_rotLimiter = new SlewRateLimiter(DriveConstants.kRotationalSlewRate);
  private double m_prevTime = WPIUtilJNI.now() * 1e-6;

  // Odometry class for tracking robot pose
  public SwerveDrivePoseEstimator m_odometry;

  // Adding MAXBRAKE
  private final static double MAX_BRAKE = 0.8;
  private final double slowSpeed = 1.0;
  private final double capSpeed = 0.75;

  public Swerve() {
   // RobotContainer.getInstance().m_HealthCheck.registerSparkMAx(String.valueOf(m_frontLeft.m_drivingSparkMax.getDeviceId()) , m_frontLeft.m_drivingSparkMax);
   // RobotContainer.getInstance().m_HealthCheck.registerSparkMAx(String.valueOf(m_frontLeft.m_turningSparkMax.getDeviceId()) , m_frontLeft.m_turningSparkMax);


    m_odometry = new SwerveDrivePoseEstimator(
        DriveConstants.kDriveKinematics,
        Rotation2d.fromDegrees(m_gyro.getAngle()),
        new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_rearLeft.getPosition(),
            m_rearRight.getPosition()
        },
        new Pose2d(0.0, 0.0, new Rotation2d()));
    // All other subsystem initialization
    // ...
    // Configure AutoBuilder last

    AutoBuilder.configureHolonomic(
        this::getPose, // Robot pose supplier
        this::resetOdometry, // Method to reset odometry (will be called if your auto has a starting pose)
        this::getChassisSpeeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
        this::driveRobotRelative, // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds
        new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your Constants class
            new PIDConstants(Constants.AutoConstants.kDrivingP, Constants.AutoConstants.kDrivingI,
                Constants.AutoConstants.kDrivingD), // Translation PID constants
            new PIDConstants(Constants.AutoConstants.kTurningP, Constants.AutoConstants.kTurningI,
                Constants.AutoConstants.kTurningD), // Rotation PID constants
            Constants.DriveConstants.kMaxSpeedMetersPerSecond, // Max module speed, in m/s
            Constants.DriveConstants.kRadius_meters, // Drive base radius in meters. Distance from robot center to
                                                     // furthest module.
            new ReplanningConfig() // Default path replanning config. See the API for the options here
        ),
        () -> {
          // Boolean supplier that controls when the path will be mirrored for the red
          // alliance
          // This will flip the path being followed to the red side of the field.
          // THE ORIGIN WILL REMAIN ON THE BLUE SIDE
          return RobotContainer.getInstance().isRed();
        },
        this // Reference to this subsystem to set requirements
    );

    


  }

  @Override
  public void periodic() {

    m_odometry.update(
        Rotation2d.fromDegrees(m_gyro.getAngle()),
        new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_rearLeft.getPosition(),
            m_rearRight.getPosition()
        });

  }

  public void addVision(Pose2d currPose2d, double timeStamp) {
    m_odometry.addVisionMeasurement(currPose2d, timeStamp);
  }

  /**
   * Returns the currently-estimated pose of the robot.
   *
   * @return The pose.
   */
  public Pose2d getPose() {
    return m_odometry.getEstimatedPosition();
  }

  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    m_odometry.resetPosition(
        Rotation2d.fromDegrees(m_gyro.getAngle()),
        new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_rearLeft.getPosition(),
            m_rearRight.getPosition()
        },
        pose);
  }

  /**
   * Method to drive the robot using joystick info.
   *
   * @param xSpeed        Speed of the robot in the x direction (forward).
   * @param ySpeed        Speed of the robot in the y direction (sideways).
   * @param rot           Angular rate of the robot.
   * @param fieldRelative Whether the provided x and y speeds are relative to the
   *                      field.
   * @param rateLimit     Whether to enable rate limiting for smoother control.
   */
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative, boolean rateLimit) {

    double xSpeedCommanded;
    double ySpeedCommanded;

    if (rateLimit) {
      // Convert XY to polar for rate limiting
      double inputTranslationDir = Math.atan2(ySpeed, xSpeed);
      double inputTranslationMag = Math.sqrt(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2));

      // Calculate the direction slew rate based on an estimate of the lateral
      // acceleration
      double directionSlewRate;
      if (m_currentTranslationMag != 0.0) {
        directionSlewRate = Math.abs(DriveConstants.kDirectionSlewRate / m_currentTranslationMag);
      } else {
        directionSlewRate = 500.0; // some high number that means the slew rate is effectively instantaneous
      }

      double currentTime = WPIUtilJNI.now() * 1e-6;
      double elapsedTime = currentTime - m_prevTime;
      double angleDif = SwerveUtils.AngleDifference(inputTranslationDir, m_currentTranslationDir);
      if (angleDif < 0.45 * Math.PI) {
        m_currentTranslationDir = SwerveUtils.StepTowardsCircular(m_currentTranslationDir, inputTranslationDir,
            directionSlewRate * elapsedTime);
        m_currentTranslationMag = m_magLimiter.calculate(inputTranslationMag);
      } else if (angleDif > 0.85 * Math.PI) {
        if (m_currentTranslationMag > 1e-4) { // some small number to avoid floating-point errors with equality checking
          // keep currentTranslationDir unchanged
          m_currentTranslationMag = m_magLimiter.calculate(0.0);
        } else {
          m_currentTranslationDir = SwerveUtils.WrapAngle(m_currentTranslationDir + Math.PI);
          m_currentTranslationMag = m_magLimiter.calculate(inputTranslationMag);
        }
      } else {
        m_currentTranslationDir = SwerveUtils.StepTowardsCircular(m_currentTranslationDir, inputTranslationDir,
            directionSlewRate * elapsedTime);
        m_currentTranslationMag = m_magLimiter.calculate(0.0);
      }
      m_prevTime = currentTime;

      xSpeedCommanded = m_currentTranslationMag * Math.cos(m_currentTranslationDir);
      ySpeedCommanded = m_currentTranslationMag * Math.sin(m_currentTranslationDir);
      m_currentRotation = m_rotLimiter.calculate(rot);

    } else {
      xSpeedCommanded = xSpeed;
      ySpeedCommanded = ySpeed;
      m_currentRotation = rot;
    }

    // Convert the commanded speeds into the correct units for the drivetrain
    double xSpeedDelivered = xSpeedCommanded * DriveConstants.kMaxSpeedMetersPerSecond;
    double ySpeedDelivered = ySpeedCommanded * DriveConstants.kMaxSpeedMetersPerSecond;
    double rotDelivered = m_currentRotation * DriveConstants.kMaxAngularSpeed;

    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(
        fieldRelative
            ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeedDelivered, ySpeedDelivered, rotDelivered,
                Rotation2d.fromDegrees(m_gyro.getAngle()))
            : new ChassisSpeeds(xSpeedDelivered, ySpeedDelivered, rotDelivered));
    SwerveDriveKinematics.desaturateWheelSpeeds(
        swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_rearLeft.setDesiredState(swerveModuleStates[2]);
    m_rearRight.setDesiredState(swerveModuleStates[3]);
  }

  /**
   * Sets the wheels into an X formation to prevent movement.
   */
  public void setX() {
    m_frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
    m_frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
    m_rearLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)));
    m_rearRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)));
  }

  /**
   * Sets the swerve ModuleStates.
   *
   * @param desiredStates The desired SwerveModule states.
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    SwerveDriveKinematics.desaturateWheelSpeeds(
        desiredStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(desiredStates[0]);
    m_frontRight.setDesiredState(desiredStates[1]);
    m_rearLeft.setDesiredState(desiredStates[2]);
    m_rearRight.setDesiredState(desiredStates[3]);
  }

  /** Resets the drive encoders to currently read a position of 0. */
  public void resetEncoders() {
    m_frontLeft.resetEncoders();
    m_rearLeft.resetEncoders();
    m_frontRight.resetEncoders();
    m_rearRight.resetEncoders();
  }

  /** Zeroes the heading of the robot. */
  public void zeroHeading() {
    m_gyro.reset();
  }

  /**
   * Returns the heading of the robot.
   *
   * @return the robot's heading in degrees, from -180 to 180
   */
  public double getHeading() {
    return Rotation2d.fromDegrees(m_gyro.getAngle()).getDegrees();
  }

  /**
   * Returns the turn rate of the robot.
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    return m_gyro.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }

  // This should stop the motors.
  public void stopDrive() {
    // Stop the motors and the turning NOW.
    drive(0, 0, 0, true, true);
  }

  // Get the distance traveled from a start pose2d
  public double getDistanceTraveledInches(Pose2d startPose2d) {
    Pose2d curPose_Meters = m_odometry.getEstimatedPosition();
    return (RobotMath.metersToInches(curPose_Meters.getTranslation().getDistance(startPose2d.getTranslation())));
  }

  public void cmdTeleOp() {
    // Apply Deadband
    double leftY = -MathUtil.applyDeadband(RobotContainer.getInstance().m_driverController.getLeftY(),
        OIConstants.kDriveDeadband);
    double leftX = -MathUtil.applyDeadband(RobotContainer.getInstance().m_driverController.getLeftX(),
        OIConstants.kDriveDeadband);
    double rightX = -MathUtil.applyDeadband(RobotContainer.getInstance().m_driverController.getRightX(),
        OIConstants.kDriveDeadband);

    // Adding brake
    /*
     * leftY = leftY - (Math.signum(leftY) * CommonLogic.CapMotorPower(
     * RobotContainer.getInstance().m_driverController.getLeftTriggerAxis(),0,
     * MAX_BRAKE));
     * leftX = leftX - (Math.signum(leftX) * CommonLogic.CapMotorPower(
     * RobotContainer.getInstance().m_driverController.getLeftTriggerAxis(),0,
     * MAX_BRAKE));
     * rightX = rightX - (Math.signum(rightX) * CommonLogic.CapMotorPower(
     * RobotContainer.getInstance().m_driverController.getLeftTriggerAxis(),0,
     * MAX_BRAKE));
     */
    if (RobotContainer.getInstance().m_driverController.getLeftTriggerAxis() >= 0.5) {
      leftY = leftY * slowSpeed;
      leftX = leftX * slowSpeed;
      rightX = rightX * capSpeed;
    } else {
      leftY = leftY * capSpeed;
      leftX = leftX * capSpeed;
      rightX = rightX * capSpeed;
    }
    // square them to make them usefully curved
    leftY = Math.signum(leftY) * leftY * leftY;
    leftX = Math.signum(leftX) * leftX * leftX;
    rightX = Math.signum(rightX) * rightX * rightX;
    if (RobotContainer.getInstance().m_driverController.a().getAsBoolean()){
      turn(0, leftX, leftY);
    } else if (RobotContainer.getInstance().m_driverController.b().getAsBoolean()) {
      turn(90,leftX, leftY);
    }else if (RobotContainer.getInstance().m_driverController.y().getAsBoolean()) { 
    // Drive the bot
    turn(RobotContainer.getInstance().m_cam1.getSpeakerDEG(), leftX, leftY);
  }else {

    RobotContainer.getInstance().m_robotDrive.drive(leftY, leftX, rightX, true, true);
  }
  }

  public ChassisSpeeds getChassisSpeeds() {

    return DriveConstants.kDriveKinematics.toChassisSpeeds(m_frontLeft.getState(), m_frontRight.getState(),
        m_rearLeft.getState(), m_rearRight.getState());

  }

  // wrap the drive command with a function that accepts a ChassisSpeed Object
  public void driveRobotRelative(ChassisSpeeds cs) {

    var swerveModuleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(cs);
    SwerveDriveKinematics.desaturateWheelSpeeds(
        swerveModuleStates, DriveConstants.kMaxSpeedMetersPerSecond);
    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_rearLeft.setDesiredState(swerveModuleStates[2]);
    m_rearRight.setDesiredState(swerveModuleStates[3]);
  }

  // used to put data to the dashboard
  public String getPose2dString() {

    double x = m_odometry.getEstimatedPosition().getX();
    double y = m_odometry.getEstimatedPosition().getY();
    double deg = m_odometry.getEstimatedPosition().getRotation().getDegrees();

    if (Double.isNaN(x)) {
      x = 99;
    }

    if (Double.isNaN(y)) {
      y = 99;
    }

    if (Double.isNaN(deg)) {
      deg = 999;
    }

    return String.format("odometry: %.2f    %.2f    %.0f", x, y, deg);

  }

  public void turn(double heading, double leftX, double leftY) {
    double targetHeading = heading;
    double targetHeadingRAD = 0;
    double current = Math.toRadians(m_gyro.getNormaliziedNavxAngle());
    double stepSizeRAD = Math.toRadians(1); 
   if (!RobotContainer.getInstance().isRed()) {
      targetHeading = -targetHeading;
    } 
    
    
    // double current = this.m_odometry.getEstimatedPosition().getRotation()
    // .getRadians();

      /*if(m_gyro.gyroInTol(Math.toDegrees(current), Math.toDegrees(targetHeadingRAD), 3)){
       // this.stopDrive();
     } else {*/
     
    this.drive(leftY, leftX,
        (CommonLogic.CapMotorPower(CommonLogic.gotoPosPIDF
        (0.008,0,RobotContainer.getInstance().m_robotDrive.m_gyro.getNormaliziedNavxAngle(), targetHeading),
            minTurnPow, maxTurnPow)),
        true, false);
    // }
    String msg = String.format("Current: %.4f Target: %.4f",
        current, targetHeadingRAD);
    System.err.println(msg);
  }

  public Command followPathCommand(String pathName) {
    PathPlannerPath path = PathPlannerPath.fromPathFile(pathName);

    return new FollowPathHolonomic(
        path,
        this::getPose, // Robot pose supplier
        this::getChassisSpeeds, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
        this::driveRobotRelative, // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds
        new HolonomicPathFollowerConfig( // HolonomicPathFollowerConfig, this should likely live in your Constants class
            new PIDConstants(Constants.AutoConstants.kDrivingP,
                Constants.AutoConstants.kDrivingI,
                Constants.AutoConstants.kDrivingD), // Translation PID constants
            new PIDConstants(Constants.AutoConstants.kTurningP, Constants.AutoConstants.kTurningI,
                Constants.AutoConstants.kTurningD), // Rotation PID constants
            Constants.DriveConstants.kMaxSpeedMetersPerSecond, // Max module speed, in m/s
            Constants.DriveConstants.kRadius_meters, // Drive base radius in meters. Distance from robot center to
                                                     // furthest module.
            new ReplanningConfig() // Default path replanning config. See the API for the options here
        ),
        () -> {
          // Boolean supplier that controls when the path will be mirrored for the red
          // alliance
          // This will flip the path being followed to the red side of the field.
          // THE ORIGIN WILL REMAIN ON THE BLUE SIDE
            return RobotContainer.getInstance().isRed();
        },
        this // Reference to this subsystem to set requirements
    );
  }
}
