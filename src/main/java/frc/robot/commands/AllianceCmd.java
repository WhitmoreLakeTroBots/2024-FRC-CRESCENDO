package frc.robot.commands;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;

public class AllianceCmd extends Command {
    private boolean bDone = false;
    private DriverStation.Alliance alliance;
    public AllianceCmd(DriverStation.Alliance AL) {
        alliance = AL;
        // m_subsystem = subsystem;
        // addRequirements(m_subsystem);

    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    //System.err.println("alliance command inizializing");
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        RobotContainer.getInstance().setAlliance(alliance);
        //System.err.println("executing alliancecmd");
        bDone = true;
       // end(bDone);
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
        return true;

    }
}
