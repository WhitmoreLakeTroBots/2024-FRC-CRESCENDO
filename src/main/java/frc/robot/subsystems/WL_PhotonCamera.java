package frc.robot.subsystems;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;

import java.io.IOException;
import java.util.Optional;

import org.photonvision.*;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.proto.Wpimath;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.utils.CommonLogic;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;


public class WL_PhotonCamera extends SubsystemBase{
    private AprilTagFieldLayout aprilTagFieldLayout = null;
    private PhotonCamera cam;
    private PhotonPipelineResult result;
    private PhotonTrackedTarget target;
    private Transform3d robot2CameraTransform;
    private double timeStamp = 0;
    private Pose3d currPose3d;
    private String msg = "";
    private String camName = "";

    public WL_PhotonCamera (PhotonCamera cam , Transform3d cam2robot_transform_3d){
        camName = cam.getName();
        this.cam = cam;
        robot2CameraTransform = cam2robot_transform_3d;
        timeStamp = 0;

        try {
            aprilTagFieldLayout = AprilTagFieldLayout
                    .loadFromResource(AprilTagFields.k2024Crescendo.m_resourceFile);
            // The parameter for loadFromResource() will be different depending on the game.
        } catch (IOException e) {
            System.err.println("Could not load april tag field layout");
            System.out.println(e);
        }
    }


    // this gets put in the periodic to cause the rPi to compute a pose
    // this updates all of the camera values and timestamps them
    public void periodic () {
        result = cam.getLatestResult();

        // multiple tags are visible with one camera... Best case use them both
        if (result.getMultiTagResult().estimatedPose.isPresent) {
            Transform3d fieldToCamera = result.getMultiTagResult().estimatedPose.best;
            // https://docs.wpilib.org/en/stable/docs/software/basic-programming/coordinate-system.html
            // https://docs.photonvision.org/en/latest/docs/apriltag-pipelines/coordinate-systems.html

            currPose3d = new Pose3d (fieldToCamera.plus(robot2CameraTransform).getTranslation(),
                                     fieldToCamera.plus(robot2CameraTransform).getRotation());

            msg = String.format ("Cam: %s Tag: %s X: %.2f Y: %.2f Yaw: %.0f",
                    camName, result.getMultiTagResult().fiducialIDsUsed.toString(),
                    currPose3d.getX(), currPose3d.getY(), currPose3d.getRotation().getZ());
        }
        // only one tag visible... use it but it is not as accurate as multiple tags.
        else if (result.hasTargets()) {
            Optional <Pose3d> bestTagPose = aprilTagFieldLayout.getTagPose(result.getBestTarget().getFiducialId());
            if (bestTagPose.isPresent()) {
                timeStamp = CommonLogic.getTime();
                target = result.getBestTarget();
                currPose3d = PhotonUtils.estimateFieldToRobotAprilTag(
                        target.getBestCameraToTarget(),
                        aprilTagFieldLayout.getTagPose(target.getFiducialId()).orElse(null),
                        // aprilTagFieldLayout.getTagPose(m_tag_ID).orElse(null),
                        robot2CameraTransform);

                msg = String.format ("Cam: %s Tag: %s X: %.2f Y: %.2f Yaw: %.0f",
                    camName, target.getFiducialId(),
                    currPose3d.getX(), currPose3d.getY(), currPose3d.getRotation().getZ());
            }
            else {
                msg = String.format("%s, Invalid Tag ID", camName);
            }
        }

        else {
            msg = String.format("%s, No Tags Visible.", camName);
        }
    }

    public boolean hasTargets (){
        return result.hasTargets();
    }

    public Pose3d getRobotPose3d() {
        return currPose3d;
    }

    public double getTimeStamp() {
        return timeStamp;
    }

    public double getLatencyMillis() {
        return result.getLatencyMillis();
    }
    public String toString() {
        return msg;
    }

}
