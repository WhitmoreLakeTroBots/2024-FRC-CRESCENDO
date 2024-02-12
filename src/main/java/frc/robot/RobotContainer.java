package frc.robot;

import frc.robot.commands.intakeCommands.*;
//import frc.robot.commands.*;
import frc.robot.commands.LauncherCommands.*;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Intake.RollerStatus;
import frc.robot.subsystems.Launcher.ANGLEPOS;
import frc.robot.subsystems.Launcher.LauncherModes;

import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;

import edu.wpi.first.wpilibj2.command.Command;
//import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
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
    public final SubPoseEstimator m_Photon = new SubPoseEstimator();
    public final Swerve m_robotDrive = new Swerve();
    public final Intake m_Intake = new Intake();
    public final Sensors m_Sensors = new Sensors();
    public final Launcher m_Launcher = new Launcher();
    public final Lighting m_Lighting = new Lighting();
    SendableChooser<Command> m_Chooser = new SendableChooser<>();
    // The driver's controller
    public final CommandXboxController m_driverController = new CommandXboxController(0);
    public final CommandXboxController m_articController = new CommandXboxController(1);

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        // Configure the button bindings

        configureButtonBindings();
        m_Chooser.addOption("Test Path Planner Auto", new PathPlannerAuto("test_auto"));
        m_Chooser.addOption("Test Path Straight", new PathPlannerAuto("test_auto2"));

        SmartDashboard.putData("Auto Mode", m_Chooser);
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
        A_drive.onTrue(new AngleCmd(ANGLEPOS.AMP, false));

        Trigger B_drive = m_driverController.b();

        Trigger X_drive = m_driverController.x();
        X_drive.whileTrue(new RunCommand(
                () -> m_robotDrive.setX(),
                m_robotDrive));
        Trigger Y_drive = m_driverController.y();

        Trigger DUp_drive = m_driverController.povUp();

        Trigger DLeft_drive = m_driverController.povLeft();

        Trigger DDown_drive = m_driverController.povDown();

        Trigger DRight_drive = m_driverController.povRight();

        Trigger BACK_drive = m_driverController.back();
        BACK_drive.whileTrue(new RunCommand(
                () -> m_robotDrive.zeroHeading()));
        Trigger START_drive = m_driverController.start();

        Trigger LBump_drive = m_driverController.leftBumper();

        //Trigger LTrig_drive = m_driverController.leftTrigger();
        //Left Trigger being used as brake, see Swerve class for details
       
        Trigger RBump_drive = m_driverController.rightBumper();
        RBump_drive.onTrue(new pivotCmd(Intake.PivotPos.OUT, false))
        .onTrue(new intakeCmd(RollerStatus.FORWARD));
        
        Trigger RTrig_drive = m_driverController.rightTrigger();
        RTrig_drive.onTrue(new intakeCmd(Intake.RollerStatus.REVERSE));
        RTrig_drive.onFalse(new intakeCmd(RollerStatus.STOP));
//Articulion Controller*************************************************
        Trigger A_Artic = m_articController.a();
        A_Artic.onTrue(new AngleCmd(ANGLEPOS.AMP, false));

        Trigger B_Artic = m_articController.b();
        B_Artic.onTrue(new AngleCmd(ANGLEPOS.START, false));

        Trigger X_Artic = m_articController.x();
        Trigger Y_Artic = m_articController.y();

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
        SmartDashboard.putNumber("heading", m_robotDrive.getHeading());
        SmartDashboard.putData("Auto Mode", m_Chooser);
        SmartDashboard.putNumber("Distance Traveled",
                m_robotDrive.getDistanceTraveledInches(new Pose2d(2.0, 7.0, new Rotation2d())));
                SmartDashboard.putNumber("PivotLocation", m_Intake.getCurPivotPos());
        SmartDashboard.putNumber("RollerStatus", m_Intake.getCurRollerStatus().getPow());
        SmartDashboard.putBoolean("BeamBreak1", m_Sensors.getBB1());
    
    
        SmartDashboard.putString("odo", m_robotDrive.getPose2dString());
        SmartDashboard.putString("visAVG", m_Photon.getPoseAVG());
        SmartDashboard.putString("vis11A", m_Photon.getPose11A());
        SmartDashboard.putString("vis11B", m_Photon.getPose11B());

        SmartDashboard.putNumber("launcher angle", m_Launcher.getAnglePosActual());

        //Launcher
        SmartDashboard.putNumber("LauncherTargetRPM", m_Launcher.getTargetRPM());
        SmartDashboard.putNumber("LauncherActualRPM", m_Launcher.getActualRPM());
        SmartDashboard.putBoolean("LaunchAngleStatus", m_Launcher.getAngleStatus());
        SmartDashboard.putNumber("LaunchTargetAngle", m_Launcher.getAnglePos().getangle());
    
}

    public static RobotContainer getInstance() {
        return m_robotContainer;

    }
}
