package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose3d;
import java.io.IOException;
import java.util.Optional;

import org.photonvision.*;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.utils.CommonLogic;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;

public class WL_PhotonCamera extends SubsystemBase {
    private AprilTagFieldLayout aprilTagFieldLayout = null;
    private PhotonCamera cam;
    private PhotonPipelineResult result;
    private PhotonTrackedTarget target;
    private Transform3d robot2CameraTransform;
    private double timeStamp = 0;
    private Pose3d currPose3d;
    private String msg = "";
    private String camName = "";
    private int ourSpeakerTag = 7;
    private double speakerAlignmentRad = 0;
    private boolean speakerTagVisible = false;

    public WL_PhotonCamera(PhotonCamera cam, Transform3d cam2robot_transform_3d) {
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
    public void periodic() {
        result = cam.getLatestResult();

        // multiple tags are visible with one camera... Best case use them both
        if (result.getMultiTagResult().estimatedPose.isPresent) {
            Transform3d fieldToCamera = result.getMultiTagResult().estimatedPose.best;
            // https://docs.wpilib.org/en/stable/docs/software/basic-programming/coordinate-system.html
            // https://docs.photonvision.org/en/latest/docs/apriltag-pipelines/coordinate-systems.html

            currPose3d = new Pose3d(fieldToCamera.plus(robot2CameraTransform).getTranslation(),
                    fieldToCamera.plus(robot2CameraTransform).getRotation());

            msg = String.format("Tag: %s X: %.2f Y: %.2f Yaw: %.0f",
                    result.getMultiTagResult().fiducialIDsUsed.toString(),
                    currPose3d.getX(), currPose3d.getY(), Math.toDegrees(currPose3d.getRotation().getZ()));
        }

        // only one tag visible... use it but it is not as accurate as multiple tags.
        else if (result.hasTargets()) {
            Optional<Pose3d> bestTagPose = aprilTagFieldLayout.getTagPose(result.getBestTarget().getFiducialId());
            if (bestTagPose.isPresent()) {
                timeStamp = CommonLogic.getTime();
                target = result.getBestTarget();
                currPose3d = PhotonUtils.estimateFieldToRobotAprilTag(
                        target.getBestCameraToTarget(),
                        aprilTagFieldLayout.getTagPose(target.getFiducialId()).orElse(null),
                        // aprilTagFieldLayout.getTagPose(m_tag_ID).orElse(null),
                        robot2CameraTransform);

                msg = String.format("Tag: %s X: %.2f Y: %.2f Yaw: %.1f %.3f",
                        target.getFiducialId(),
                        currPose3d.getX(), currPose3d.getY(), Math.toDegrees(currPose3d.getRotation().getZ()),
                        result.getLatencyMillis());
            } else {
                msg = String.format("Invalid Tag ID");
            }

            speakerTagVisible = false;
            result.targets.forEach(tag -> {
                if (tag.getFiducialId() == ourSpeakerTag) {
                    speakerAlignmentRad = tag.getYaw();
                    speakerTagVisible = true;
                }
            });

            if (! speakerTagVisible) {
                speakerAlignmentRad = 0;
            }
        }

        else {
            msg = String.format("No Tags Visible.");
        }
    }

    public String getCamName() {
        return this.camName;
    }

    public boolean hasTargets() {
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

    public void setRed(boolean isRed) {
        if (isRed) {
            ourSpeakerTag = 4;
        } else {
            ourSpeakerTag = 7;
        }
    }
    public double getSpeakerDEG(){
        return Math.toDegrees(speakerAlignmentRad);
    }

}
