package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.utils.RobotMath;
//import edu.wpi.first.apriltag.*;

import java.io.IOException;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import org.photonvision.*;
import org.photonvision.targeting.PhotonTrackedTarget;
import java.util.List;

import java.util.Optional;

/**
 *
 */
public class SubPoseEstimator extends SubsystemBase {
    Pose3d robotFieldPose = null;
  //  private final Pose3d nullPose = new Pose3d(new Translation3d(-99, -99, -99), new Rotation3d(0.0, 0.0, 0.0));
    private final PhotonCamera cam12 = new PhotonCamera("10.36.68.12");

    // default camera position
    private final Transform3d cam12_2_robotTransform3d = new Transform3d(new Translation3d(0, 0, .45),
            new Rotation3d(Math.toRadians(0), Math.toRadians(0), Math.toRadians(9)));
    // The parameter for loadFromResource() will be different depending on the game.
    private AprilTagFieldLayout aprilTagFieldLayout = null;

    private double m_cam12_x = 0.0;
    private double m_cam12_y = 0.0;
    private double m_cam12_z = 0.0;

    private int m_tag_ID = 0;
    private double m_field_x = 0.0;
    private double m_field_y = 0.0;
    private double m_field_z = 0.0;

    private double m_field_rollRad = 0.0;
    private double m_field_yawRad = 0.0;
    private double m_field_pitchRad = 0.0;

    private Boolean m_HasTargets = false;

    public int grid = 0;
    public int column = 0;

    public double diffX = 0;
    public double diffY = 0;

    public double calcDiffX;
    public double calcDiffY;

    public List<PhotonTrackedTarget> tags;

    public SubPoseEstimator() {

        try {
            aprilTagFieldLayout = AprilTagFieldLayout
                    .loadFromResource(AprilTagFields.k2023ChargedUp.m_resourceFile);
        } catch (IOException e) {
            System.err.println("Could not load april tag field layout");
            System.out.println(e);
        }

    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        tags = cam12.getLatestResult().getTargets();
        processTags();
    }

    @Override
    public void simulationPeriodic() {
        // This method will be called once per scheduler run when in simulation

    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    /*private void NullThePose() {
       // robotFieldPose = nullPose;
        m_field_x = robotFieldPose.getX();
        m_field_y = robotFieldPose.getY();
        m_field_z = robotFieldPose.getZ();
        m_tag_ID = 0;

        m_field_rollRad = robotFieldPose.getRotation().getX();
        m_field_yawRad = robotFieldPose.getRotation().getZ();
        m_field_pitchRad = robotFieldPose.getRotation().getY();

        m_cam12_x = 0.0;
        m_cam12_y = 0.0;
        m_cam12_z = 0.0;
        m_HasTargets = false;
    }*/

    public Pose3d getRobotFieldPose() {
        return robotFieldPose;
    }

    public int getFiducialId() {
        return m_tag_ID;
    }

    public double getCameraX() {
        return m_cam12_x;
    }

    public double getCameraY() {
        return m_cam12_y;
    }

    public double getCameraZ() {
        return m_cam12_z;
    }

    public double getFieldX() {
        return m_field_x;
    }

    public double getFieldY() {
        return m_field_y;
    }

    public double getFieldZ() {
        return m_field_z;
    }

    public double getFieldRollRad() {
        return m_field_rollRad;
    }

    public double getFieldYawRad() {
        return m_field_yawRad;
    }

    public double getFieldPitchRad() {
        return m_field_pitchRad;
    }

    private void processTags(){
        var results = cam12.getLatestResult();

        if (results.hasTargets()) {
            m_tag_ID = results.getBestTarget().getFiducialId();
            Optional<Pose3d> bestTagPose = aprilTagFieldLayout.getTagPose(m_tag_ID);

            if (bestTagPose.isPresent()) {
                m_HasTargets = results.hasTargets();
                robotFieldPose = PhotonUtils.estimateFieldToRobotAprilTag(
                        results.getBestTarget().getBestCameraToTarget(),
                        bestTagPose.get(),
                        cam12_2_robotTransform3d);

                m_field_x = robotFieldPose.getX();
                m_field_y = robotFieldPose.getY();
                m_field_z = robotFieldPose.getZ();
                m_field_rollRad = robotFieldPose.getRotation().getX();
                m_field_yawRad = robotFieldPose.getRotation().getZ();
                m_field_pitchRad = robotFieldPose.getRotation().getY();

                m_cam12_x = results.getBestTarget().getBestCameraToTarget().getX();
                m_cam12_y = results.getBestTarget().getBestCameraToTarget().getY();
                m_cam12_z = results.getBestTarget().getBestCameraToTarget().getZ();

            } 
        }
    }

    public double getDistanceFromTagInInches(int tagID){
        double distance = -99;
        try{
            for(int i=0;i<tags.size();i++){
                if(tags.get(i).getFiducialId() == tagID){
                    distance = tags.get(i).getBestCameraToTarget().getX();
                }
            }
            if(distance != -99){
                return RobotMath.metersToInches(distance);
            }
            else{return -99;}
        }
        catch(Exception e){return -99;}
    }

}
