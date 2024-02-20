package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Rotation3d;

import com.revrobotics.CANSparkBase.IdleMode;

public class Constants {

  // Rotation3d (roll, pitch, yaw ) // in radians
  public static final class Cam1Constants {
    public static final String name = "Cam11A";
    public static final Transform3d cam2robotTransform3d = new Transform3d (0.0, -0.3, 0.3,
      new Rotation3d (Math.toRadians(0.0), Math.toRadians(45.0), Math.toRadians(0)));
  }

  public static final class Cam2Constants {
    public static final String name = "Cam11B";
    public static final Transform3d cam2robotTransform3d = new Transform3d (0.0, 0.0, 1.0,
      new Rotation3d (Math.toRadians(0.0), Math.toRadians(0.0), Math.toRadians(0.0) ));

  }

  public static final class DriveConstants {

    public static final double kMaxSpeedMetersPerSecond = 6.0;
    public static final double kMaxAngularSpeed = 4 * Math.PI; // radians per second

    public static final double kDirectionSlewRate = 5.0; // radians per second
    public static final double kMagnitudeSlewRate = 7.0; // percent per second (1 = 100%)
    public static final double kRotationalSlewRate = 5.0; // percent per second (1 = 100%)

    // Chassis configuration
    public static final double kTrackWidth = Units.inchesToMeters(20.5); // Distance between centers of right and left
                                                                         // wheels on robot
    public static final double kWheelBase = Units.inchesToMeters(20.5);
    // calcuate a radius based on TrackWidth and Wheelbase
    public static final double kRadius_meters = Math.sqrt(Math.pow(kTrackWidth / 2, 2) + Math.pow(kWheelBase / 2, 2));

    public static final SwerveDriveKinematics kDriveKinematics = new SwerveDriveKinematics(
        new Translation2d(kWheelBase / 2, kTrackWidth / 2),
        new Translation2d(kWheelBase / 2, -kTrackWidth / 2),
        new Translation2d(-kWheelBase / 2, kTrackWidth / 2),
        new Translation2d(-kWheelBase / 2, -kTrackWidth / 2));

    // Angular offsets of the modules relative to the chassis in radians
    public static final double kFrontLeftChassisAngularOffset = -Math.PI / 2;
    public static final double kFrontRightChassisAngularOffset = 0;
    public static final double kBackLeftChassisAngularOffset = Math.PI;
    public static final double kBackRightChassisAngularOffset = Math.PI / 2;

    public static final boolean kGyroReversed = false;
  }

  public static final class CANIDs {
    // Drive Motor Can IDS
    public static final int kFrontLeftDrivingCanId = 5;
    public static final int kRearLeftDrivingCanId = 3;
    public static final int kFrontRightDrivingCanId = 1;
    public static final int kRearRightDrivingCanId = 7;
    // Turn Motor Can IDS
    public static final int kFrontLeftTurningCanId = 6;
    public static final int kRearLeftTurningCanId = 4;
    public static final int kFrontRightTurningCanId = 2;
    public static final int kRearRightTurningCanId = 8;

    // Intake Motor Can IDS
    public static final int RotMotorId = 10;
    public static final int PivMotorId = 9;

    //Launcher Motors Can IDs
    public static final int LauncherMotorTopId = 11;
    public static final int LauncherMotorBottomId = 12;
    public static final int FeederMotorId = 13;
    public static final int LaunchAngleMotorId = 14;

    //Climb Motors Can IDs
    public static final int ClimbMotorLeftId = 15;
    public static final int ClimbMotorRightId = 16;

  }

  public static final class ModuleConstants {

    public static final int kDrivingMotorPinionTeeth = 17;

    // Invert the turning encoder, since the output shaft rotates in the opposite
    // direction of
    // the steering motor in the MAXSwerve Module.
    public static final boolean kTurningEncoderInverted = true;

    // Calculations required for driving motor conversion factors and feed forward
    public static final double kDrivingMotorFreeSpeedRps = NeoMotorConstants.kFreeSpeedRpm / 60;
    public static final double kWheelDiameterMeters = 0.0762;
    public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;
    // 45 teeth on the wheel's bevel gear, 22 teeth on the first-stage spur gear, 15
    // teeth on the bevel pinion
    public static final double kDrivingMotorReduction = (45.0 * 19) / (kDrivingMotorPinionTeeth * 15);
    public static final double kDriveWheelFreeSpeedRps = (kDrivingMotorFreeSpeedRps * kWheelCircumferenceMeters)
        / kDrivingMotorReduction;

    public static final double kDrivingEncoderPositionFactor = (kWheelDiameterMeters * Math.PI)
        / kDrivingMotorReduction; // meters
    public static final double kDrivingEncoderVelocityFactor = ((kWheelDiameterMeters * Math.PI)
        / kDrivingMotorReduction) / 60.0; // meters per second

    public static final double kTurningEncoderPositionFactor = (2 * Math.PI); // radians
    public static final double kTurningEncoderVelocityFactor = (2 * Math.PI) / 60.0; // radians per second

    public static final double kTurningEncoderPositionPIDMinInput = 0; // radians
    public static final double kTurningEncoderPositionPIDMaxInput = kTurningEncoderPositionFactor; // radians

    public static final double kDrivingP = 0.05;
    public static final double kDrivingI = 0.0;
    public static final double kDrivingD = 0;
    public static final double kDrivingFF = 1 / kDriveWheelFreeSpeedRps;
    public static final double kDrivingMinOutput = -1;
    public static final double kDrivingMaxOutput = 1;

    public static final double kTurningP = 1.0;
    public static final double kTurningI = 0;
    public static final double kTurningD = 0;
    public static final double kTurningFF = 0;
    public static final double kTurningMinOutput = -1;
    public static final double kTurningMaxOutput = 1;

    public static final IdleMode kDrivingMotorIdleMode = IdleMode.kBrake;
    public static final IdleMode kTurningMotorIdleMode = IdleMode.kBrake;

    public static final int kDrivingMotorCurrentLimit = 50; // amps
    public static final int kTurningMotorCurrentLimit = 30; // amps
  }

  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
    public static final double kDriveDeadband = 0.02;
  }

  public static final class AutoConstants {
    public static final double kMaxSpeedMetersPerSecond = 4.11;
    public static final double kMaxAccelerationMetersPerSecondSquared = 3;
    public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI;
    public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.PI;

    public static final double kDrivingP = 0.05;
    public static final double kDrivingI = 0.0;
    public static final double kDrivingD = 0;
    public static final double kDrivingFF = 1 / ModuleConstants.kDriveWheelFreeSpeedRps;

    public static final double kTurningP = 1.0;
    public static final double kTurningI = 0;
    public static final double kTurningD = 0;
    public static final double kTurningFF = 0;

    public static final double kPXController = 1;
    public static final double kPYController = 1;
    public static final double kPThetaController = 1;

    // Constraint for the motion profiled robot angle controller
    public static final TrapezoidProfile.Constraints kThetaControllerConstraints = new TrapezoidProfile.Constraints(
        kMaxAngularSpeedRadiansPerSecond, kMaxAngularSpeedRadiansPerSecondSquared);
  }

  public static final class NeoMotorConstants {
    public static final double kFreeSpeedRpm = 5676;
  }

  public static final class PWM {
    public static final int ledDriver = 0;
  }

}
