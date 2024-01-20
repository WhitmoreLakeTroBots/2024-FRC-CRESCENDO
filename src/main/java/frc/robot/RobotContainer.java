package frc.robot;

//import frc.robot.commands.*;
import frc.robot.subsystems.*;

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
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    // The robot's subsystems
    public final Swerve m_robotDrive = new Swerve();
    private static RobotContainer m_robotContainer = new RobotContainer();
    SendableChooser<Command> m_Chooser = new SendableChooser<>();
    // The driver's controller
    public final CommandXboxController m_driverController = new CommandXboxController(0);

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
        Trigger X_drive = m_driverController.x();
        X_drive.whileTrue(new RunCommand(
                () -> m_robotDrive.setX(),
                m_robotDrive));
        Trigger BACK_drive = m_driverController.back();
        BACK_drive.whileTrue(new RunCommand(
                () -> m_robotDrive.zeroHeading()));

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
        SmartDashboard.putData("Auto Mode",m_Chooser);
        SmartDashboard.putNumber("Distance Traveled", m_robotDrive.getDistanceTraveledInches(new Pose2d(2.0, 7.0, new Rotation2d())));
        SmartDashboard.putNumber("X pos", m_robotDrive.m_odometry.getPoseMeters().getX());
        SmartDashboard.putNumber("Y pos", m_robotDrive.m_odometry.getPoseMeters().getY());
        SmartDashboard.putNumber("Rotation", m_robotDrive.m_odometry.getPoseMeters().getRotation().getDegrees());
    }   


    public static RobotContainer getInstance() {
        return m_robotContainer;

    }
}

