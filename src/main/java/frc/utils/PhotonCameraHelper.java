package frc.utils;

import java.util.Map;
import java.io.IOException;
import java.util.HashMap;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Transform3d;

public class PhotonCameraHelper {

    Map <String, WL_PhotonCamera> cameras = new HashMap <String, WL_PhotonCamera>();

    PhotonCameraHelper () {



    }

    public void add (String key, Transform3d t3d){

        cameras.put(key, new WL_PhotonCamera(key, t3d));

    }


    public void remove (String key) {
        cameras.remove (key);

    }

    public void periodic () {
        for (Map.Entry<String, WL_PhotonCamera> e : cameras.entrySet()){
            e.getValue().periodic();
        }
    }



}
