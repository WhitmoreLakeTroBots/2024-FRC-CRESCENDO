package frc.robot.subsystems;

import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WL_PhotonCameraHelper extends SubsystemBase{

    Map <String, WL_PhotonCamera> cameras = new HashMap <String, WL_PhotonCamera>();
    public CameraWithTags tagsVisible = CameraWithTags.NoCamerasWithTags;
    private Pose3d AveragePose3d = new Pose3d (0.0, 0.0, 0.0, new Rotation3d (0.0, 0.0, 0.0));

    public WL_PhotonCameraHelper () {


    }

    public void add (WL_PhotonCamera cam){

        cameras.put(cam.getCamName(), cam);

    }


    public void periodic () {
        CameraWithTags localCameraTags = CameraWithTags.NoCamerasWithTags;
        /*
        double sum_x = 0.0;
        double sum_y = 0.0;
        double sum_z = 0.0;
        double sum_yaw = 0.0;
        double sum_pitch = 0.0;
        double sum_roll = 0.0;
        */
        int cameras_withTags = 0;

        Pose3d sumPose = new Pose3d();
        for (Map.Entry<String, WL_PhotonCamera> e : cameras.entrySet()){
            if (e.getValue().hasTargets()) {
                localCameraTags = CameraWithTags.SomeCamerasWithTags;
                /*
                sum_x = sum_x + e.getValue().getRobotPose3d().getX();
                sum_y = sum_y + e.getValue().getRobotPose3d().getY();
                sum_z = sum_z + e.getValue().getRobotPose3d().getZ();
                sum_yaw = sum_yaw + e.getValue().getRobotPose3d().getRotation().getZ();
                sum_pitch = sum_pitch + e.getValue().getRobotPose3d().getRotation().getX();
                sum_roll = sum_roll + e.getValue().getRobotPose3d().getRotation().getY();
                */
                cameras_withTags++;
                sumPose = sumPose.plus(new Transform3d (e.getValue().getRobotPose3d().getTranslation(), e.getValue().getRobotPose3d().getRotation()));

            }
            else {
                // Currently not sure what to do here.
            }
        }

        if (cameras_withTags > 0) {
            /*AveragePose3d = new Pose3d(sum_x/cameras_withTags,        sum_y/cameras_withTags,     sum_z/cameras_withTags,
                            new Rotation3d(sum_roll/cameras_withTags, sum_pitch/cameras_withTags, sum_yaw/cameras_withTags));
            */
            AveragePose3d = sumPose.div(cameras_withTags);

            // TODO : we have vision add it to the current robot pose
        }
        else {
            AveragePose3d = new Pose3d (0.0, 0.0, 0.0, new Rotation3d (0.0, 0.0, 0.0));
        }
        tagsVisible = localCameraTags;

    }

    public String getCamString(String camName){
        String retString = "";

        try {
            retString = cameras.get(camName).toString();
        }

        catch(NullPointerException ex)
        {
            retString = String.format("%s not Found. %s", camName, cameras.size());
        }
        return retString;
    }


    public enum CameraWithTags{
        NoCamerasWithTags,
        SomeCamerasWithTags
    }
}
