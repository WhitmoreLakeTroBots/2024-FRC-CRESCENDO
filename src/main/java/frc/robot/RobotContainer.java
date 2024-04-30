package frc.robot;

import frc.robot.commands.intakeCommands.*;
import frc.robot.commands.AllianceCmd;
import frc.robot.commands.resetFaultCount;
//import frc.robot.commands.*;
import frc.robot.commands.LauncherCommands.*;
import frc.robot.commands.autonCommands.B_5;
import frc.robot.commands.autonCommands.C_CN_TN_1;
import frc.robot.commands.autonCommands.bottomTwoNote;
import frc.robot.commands.autonCommands.bottomTwoNoteDelay;
import frc.robot.commands.autonCommands.centerFourNote;
import frc.robot.commands.autonCommands.centerFourNoteBN;
import frc.robot.commands.autonCommands.centerThreeNote;
import frc.robot.commands.autonCommands.centerTwoNote;
import frc.robot.commands.autonCommands.driveStraight;
import frc.robot.commands.autonCommands.topThreeAA;
import frc.robot.commands.autonCommands.topThreeNote;
import frc.robot.commands.autonCommands.topTwoNote;
import frc.robot.commands.autonCommands.topTwoNoteDelay;
import frc.robot.commands.autonCommands.turnTest;
import frc.robot.commands.autonCommands.visionSetup;
import frc.robot.commands.climbCommands.ClimbCmd;
import frc.robot.commands.driveCommands.setVisionPoseCmd;
import frc.robot.commands.driveCommands.turnCmd;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Climb.ClimbMode;
import frc.robot.subsystems.Intake.PivotPos;
import frc.robot.subsystems.Intake.RollerStatus;
import frc.robot.subsystems.Launcher.ANGLEPOS;
import frc.robot.subsystems.Launcher.LauncherModes;
import frc.utils.CommonLogic;

import org.photonvision.PhotonCamera;

import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;

import edu.wpi.first.wpilibj2.command.Command;
//import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot
 * (including subsystems, commands, and button mappings) should be declared
 * here.
 */
public class RobotContainer {
        // The robot's subsystems
        private static RobotContainer m_robotContainer = new RobotContainer();
        // public final SubPoseEstimator m_Photon = new SubPoseEstimator();
        // public final HealthCheck m_HealthCheck = new HealthCheck();

        public final Swerve m_robotDrive = new Swerve();
        public final Intake m_Intake = new Intake();
        public final Sensors m_Sensors = new Sensors();
        public final Launcher m_Launcher = new Launcher();
        public final Lighting m_Lighting = new Lighting();
        public final WL_PhotonCamera m_cam1 = new WL_PhotonCamera(new PhotonCamera(Constants.Cam1Constants.name),
                        Constants.Cam1Constants.camrobotTransform3d);
        public final Climb m_Climb = new Climb();
        public final CommandXboxController m_driverController = new CommandXboxController(0);
        public final CommandXboxController m_articController = new CommandXboxController(1);

        public final Vibration m_Vibration = new Vibration();

        private int updateCounter = 0;

        // public final WL_PhotonCamera m_cam2 = new WL_PhotonCamera (new
        // PhotonCamera(Constants.Cam2Constants.name),
        // Constants.Cam2Constants.cam2robotTransform3d);

        public final WL_PhotonCameraHelper m_CameraHelper = new WL_PhotonCameraHelper();

        SendableChooser<Command> m_Chooser = new SendableChooser<>();
        // The driver's controller
        private DriverStation.Alliance alliance = DriverStation.Alliance.Blue;
        /**
         * The container for the robot. Contains subsystems, OI devices, and commands.
         */
        SendableChooser<Command> m_Alliance = new SendableChooser<>();

        public RobotContainer() {
                // Configure the button bindings

                configureButtonBindings();
                // m_Chooser.addOption("Test Path Planner Auto", new
                // PathPlannerAuto("test_auto"));
                // m_Chooser.addOption("Test Path Straight", new PathPlannerAuto("test_auto2"));
                m_Chooser.addOption("centerTwoNote", new centerTwoNote());
                m_Chooser.addOption("centerThreeNote", new centerThreeNote());
                // m_Chooser.addOption("visionSetup", new visionSetup());
                m_Chooser.addOption("topTwoNote", new topTwoNote());
                m_Chooser.addOption("topThreeNote", new topThreeNote());
                m_Chooser.addOption("BottomTwoNote", new bottomTwoNote());
                m_Chooser.addOption("Test Turn", new turnTest());
                m_Chooser.addOption("Test", new driveStraight());
                // m_Chooser.addOption("Center Four Note", new centerFourNote()); // does not
                // work
                m_Chooser.addOption("C Four Note BN", new centerFourNoteBN());
                m_Chooser.addOption("C Four Note TN", new C_CN_TN_1());
                m_Chooser.addOption("B 5", new B_5());
                m_Chooser.addOption("B2Delay", new bottomTwoNoteDelay());
                m_Chooser.addOption("T2Delay", new topTwoNoteDelay());
                m_Chooser.addOption("Top 3 AnnArbo", new topThreeAA());
                m_Chooser.addOption("Test4", new centerFourNote());
                SmartDashboard.putData("AnglePrestart", new AngleCmd(ANGLEPOS.PRESTART, true));
                SmartDashboard.putData("Vision Pose Update", new setVisionPoseCmd());

                SmartDashboard.putData("Auto Mode", m_Chooser);

                m_CameraHelper.add(m_cam1);
                m_cam1.setAlliance(alliance);

                // m_CameraHelper.add(m_cam2);
                m_Alliance.addOption("Red", new AllianceCmd(DriverStation.Alliance.Red));
                m_Alliance.addOption("Blue", new AllianceCmd(DriverStation.Alliance.Blue));
                SmartDashboard.putData("Alliance", m_Alliance);
        }

        /**
         * Use this method to define your button->command mappings. Buttons can be
         * created by
         * instantiating a {@link edu.wpi.first.wpilibj.GenericHID} or one of its
         * subclasses ({@link
         * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then calling
         * passing it to a
         * {@link JoystickButton}.
         */
        private void configureButtonBindings() {

                Trigger A_drive = m_driverController.a();
                // A_drive.onTrue(new AngleCmd(ANGLEPOS.AMP, false));

                Trigger B_drive = m_driverController.b();
                Trigger X_drive = m_driverController.x();
                X_drive.whileTrue(new RunCommand(
                                () -> m_robotDrive.setX(),
                                m_robotDrive));
                Trigger Y_drive = m_driverController.y();

                Trigger DUp_drive = m_driverController.povUp();
                DUp_drive.onTrue(new ClimbCmd(ClimbMode.PRECLIMB));
                Trigger DLeft_drive = m_driverController.povLeft();
                DLeft_drive.onTrue(new AngleCmd(ANGLEPOS.START, false));
                Trigger DDown_drive = m_driverController.povDown();
                DDown_drive.onTrue(new ClimbCmd(ClimbMode.HOLD));
                Trigger DRight_drive = m_driverController.povRight();
                DRight_drive.onTrue(new AngleCmd(ANGLEPOS.FULLCOURT, false));

                Trigger BACK_drive = m_driverController.back();
                BACK_drive.whileTrue(new RunCommand(
                                () -> m_robotDrive.zeroHeading()));
                Trigger START_drive = m_driverController.start();

                Trigger LBump_drive = m_driverController.leftBumper();
                LBump_drive.onTrue(new intakeCmd(Intake.RollerStatus.STOP)
                                .alongWith(new pivotCmd(PivotPos.IN, false)));
                // Trigger LTrig_drive = m_driverController.leftTrigger();
                // Left Trigger being used as brake, see Swerve class for details

                Trigger RBump_drive = m_driverController.rightBumper();
                RBump_drive.onTrue(new pivotCmd(Intake.PivotPos.OUT, false))
                                .onTrue(new intakeCmd(RollerStatus.FORWARD));

                Trigger RTrig_drive = m_driverController.rightTrigger();
                RTrig_drive.onTrue(new intakeCmd(Intake.RollerStatus.REVERSE));
                RTrig_drive.onFalse(new intakeCmd(RollerStatus.STOP));
                // Articulion Controller*************************************************
                Trigger A_Artic = m_articController.a();
                A_Artic.onTrue(new pivotCmd(PivotPos.AMP, false));

                Trigger B_Artic = m_articController.b();
                B_Artic.onTrue(new AngleCmd(ANGLEPOS.START, false));

                Trigger X_Artic = m_articController.x();
                X_Artic.onTrue(new AngleCmd(ANGLEPOS.THROW, false));

                Trigger Y_Artic = m_articController.y();
                Y_Artic.onTrue(new AngleCmd(ANGLEPOS.PASS, false));

                Trigger DUp_Artic = m_articController.povUp();
                DUp_Artic.onTrue(new AngleCmd(ANGLEPOS.UNDERSPEAKER, false));

                Trigger DLeft_Artic = m_articController.povLeft();
                DLeft_Artic.onTrue(new AngleCmd(ANGLEPOS.PODIUM, false));

                Trigger DDown_Artic = m_articController.povDown();
                DDown_Artic.onTrue(new AngleCmd(ANGLEPOS.START, false));

                Trigger DRight_Artic = m_articController.povRight();
                DRight_Artic.onTrue(new AngleCmd(ANGLEPOS.MIDRANGE, false));

                Trigger BACK_Artic = m_articController.back();
                BACK_Artic.onTrue(new intakeCmd(Intake.RollerStatus.STOP));
                Trigger START_Artic = m_articController.start();

                Trigger LBump_Artic = m_articController.leftBumper();
                LBump_Artic.onTrue(new pivotCmd(Intake.PivotPos.OUT, false));

                Trigger LTrig_Artic = m_articController.leftTrigger();
                LTrig_Artic.whileTrue(new intakeCmd(RollerStatus.REVERSE));
                LTrig_Artic.onFalse(new intakeCmd(RollerStatus.STOP));
                Trigger RBump_Artic = m_articController.rightBumper();
                RBump_Artic.onTrue(new pivotCmd(Intake.PivotPos.IN, false));

                Trigger RTrig_Artic = m_articController.rightTrigger();
                RTrig_Artic.onTrue(new intakeCmd(RollerStatus.FORWARD));

        }

        /**
         * Use this to pass the autonomous command to the main {@link Robot} class.
         *
         * @return the command to run in autonomous
         */
        public Command getAutonomousCommand() {
                return m_Chooser.getSelected();
        }

        public void updateSmartDashboard() {
                // seams this is causing command loop overruns and bogging down the roborio
                if (updateCounter % 3 == 0) {
                        SmartDashboard.putNumber("Distance Traveled",
                                        m_robotDrive.getDistanceTraveledInches(new Pose2d(2.0, 7.0, new Rotation2d())));
                        SmartDashboard.putData("Auto Mode", m_Chooser);
                        SmartDashboard.putString("odo", m_robotDrive.getPose2dString());
                        SmartDashboard.putString("visAVG", m_CameraHelper.getAveragePoseString());
                } else if (updateCounter % 3 == 1) {
                        SmartDashboard.putNumber("PivotLocation", m_Intake.getCurPivotPos());
                        SmartDashboard.putNumber("RollerStatus", m_Intake.getCurRollerStatus().getPow());
                        SmartDashboard.putNumber("LauncherTargetRPM", m_Launcher.getTargetRPM());
                        SmartDashboard.putNumber("LaunchTargetAngle", m_Launcher.getAnglePos().getangle());
                        SmartDashboard.putNumber("heading", m_robotDrive.m_gyro.getNormaliziedNavxAngle());
                } else if (updateCounter % 3 == 2) {
                        SmartDashboard.putBoolean("Gyro Connected", m_robotDrive.m_gyro.isConnected());
                        SmartDashboard.putNumber("Climb Pos", m_Climb.getCLimbPos());
                        SmartDashboard.putString(Constants.Cam1Constants.name,
                                        m_CameraHelper.getCamString(Constants.Cam1Constants.name));
                        SmartDashboard.putString(Constants.Cam2Constants.name,
                                        m_CameraHelper.getCamString(Constants.Cam2Constants.name));
                        SmartDashboard.putNumber("LauncherActualRPM", m_Launcher.getActualRPM());

                }
                // important stuff

                SmartDashboard.putBoolean("BeamBreak1", m_Sensors.getBB1());
                SmartDashboard.putBoolean("Launcher",
                                CommonLogic.isInRange(m_Launcher.getActualRPM(), m_Launcher.getTargetRPM(), 250));
                SmartDashboard.putNumber("launcher angle", m_Launcher.getAnglePosActual() - 5);

                // Launcher
                SmartDashboard.putBoolean("LaunchAngleStatus", m_Launcher.getAngleStatus());
                SmartDashboard.putNumber("TagAlign", m_cam1.getSpeakerDEG());
                // SmartDashboard.putNumber("Error Count", m_HealthCheck.getFaultCount());
                // SmartDashboard.putBoolean("Error", (m_HealthCheck.getFaultCount() == 0));
                updateCounter = updateCounter + 1;
        }

        public static RobotContainer getInstance() {
                return m_robotContainer;

        }

        public boolean isRed() {
                return (alliance == DriverStation.Alliance.Red);
        }

        public void setAlliance(DriverStation.Alliance value) {
                alliance = value;
                // m_CameraHelper.setRed(bRed);
                m_cam1.setAlliance(alliance);
                //System.err.println("setting " + alliance.toString());
                switch (alliance) {
                        case Red:
                                //System.err.println("Setting Red");
                                break;

                        case Blue:
                                //System.err.println("Setting Blue");
                                break;
                        default:
                                break;
                }
        }

        public DriverStation.Alliance getAlliance() {
                return alliance;
        }

}
