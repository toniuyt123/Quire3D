package com.Quire3D.classes;

import android.view.MotionEvent;
import android.view.View;

import com.viro.core.Camera;
import com.viro.core.Node;
import com.viro.core.Quaternion;
import com.viro.core.Vector;
import com.viro.core.ViroView;

public class OrbitCamera {
    private ViroView view;
    private Node cameraNode;
    private float radiusConst;
    private float lastKnownDeltaTheta;
    private float lastKnownDeltaPhi;
    private float thetaAngleStart;
    private float phiAngleStart;
    private int[] mOldTouchPos;
    private Vector lookAt;

    public OrbitCamera(Node cameraNode, ViroView view, float radiusConst, float thetaAngleStart, float phiAngleStart) {
        this.radiusConst = radiusConst;
        this.thetaAngleStart = thetaAngleStart;
        this.phiAngleStart = phiAngleStart;
        this.view = view;
        this.lookAt = new Vector(0f, 0f, 0f);

        Camera camera = new Camera();
        this.cameraNode = cameraNode;
        this.cameraNode.setCamera(camera);
        setCameraPosition(new Vector(0f, 0f, this.radiusConst), new Vector(0f,0f,0f));

        setListener();
    }

    public OrbitCamera(Node cameraNode, ViroView view) {
        this.radiusConst = 3f;
        this.thetaAngleStart = 0f;
        this.phiAngleStart = 90f;
        this.view = view;
        this.lookAt = new Vector(0f, 0f, 0f);

        Camera camera = new Camera();
        this.cameraNode = cameraNode;
        this.cameraNode.setCamera(camera);
        setCameraPosition(new Vector(0f, 0f, this.radiusConst), new Vector(0f,0f,0f));

        setListener();
    }

    private void setListener() {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getX();
                int y = (int)event.getY();
                int[] newTouchPos = {x, y};
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mOldTouchPos = newTouchPos;
                    case MotionEvent.ACTION_MOVE:
                        // Determine normalized dx and dy from touch positions.
                        float dx = newTouchPos[0] - mOldTouchPos[0];
                        float dy = newTouchPos[1] - mOldTouchPos[1];

                        // Normalized Coordinates (use Android's get screen width / height here to replace numbers).
                        float normalizedFingerMovedX = ((float)-dx)/1080;
                        float normalizedFingerMovedY = ((float)-dy)/1920;

                        // Normalize touch dx into an angle dTheta.
                        float rateOfChangeTheta = 30; // Change this to any speed you'd like.
                        lastKnownDeltaTheta = normalizedFingerMovedX * rateOfChangeTheta;
                        double theta = thetaAngleStart + lastKnownDeltaTheta;

                        // Normalize touch dy into an angle dPhi .
                        float rateOfChangePhi = 30; // Change this to any speed you'd like.
                        lastKnownDeltaPhi = normalizedFingerMovedY * rateOfChangePhi;
                        double phi = phiAngleStart + lastKnownDeltaPhi;

                        // Determine if values should be clamped and clamp them.
                        // Note that lastKnown delta + phi datas are still saved above irregardless of clamp.
                        // Consider saving them after the fact if needed.
                        double clampedTheta = theta % 360;
                        if (theta < 0){
                            clampedTheta = 360  + theta;
                        }
                        double clampedPhi = phi % 360;
                        if (phi < 0){
                            clampedPhi = 0;
                        }

                        // Parametrize the camera's location onto a sphere based on current phi and theta values.
                        double camZ = radiusConst * Math.cos(Math.toRadians(clampedTheta)) * Math.sin(Math.toRadians(clampedPhi));
                        double camX = radiusConst * Math.sin(Math.toRadians(clampedTheta)) * Math.sin(Math.toRadians(clampedPhi));
                        double camY = radiusConst * Math.cos(Math.toRadians(clampedPhi));
                        setCameraPosition(new Vector(camX, camY, camZ), lookAt);
                        break;
                    case MotionEvent.ACTION_UP:
                        thetaAngleStart = thetaAngleStart + lastKnownDeltaTheta;
                        phiAngleStart = phiAngleStart + lastKnownDeltaPhi;
                }

                return false;
            }
        });
    }


    public void setCameraPosition(Vector cameraPosition, Vector lookAtPosition){
        Vector dirVector = lookAtPosition.subtract(cameraPosition);
        Vector dirVectorNorm = lookAtPosition.subtract(cameraPosition).normalize();
        Vector globalforward = new Vector(0,0,-1);
        Vector globalUp = new Vector(0,1,0);

        // Calculate the Camera's Yaw from direction vector.
        Vector dirVectorNormNoY = new Vector(dirVector.x, 0, dirVector.z);
        double theta = Math.acos(dirVectorNormNoY.normalize().dot(globalforward.normalize()));
        if (dirVectorNorm.x > 0){
            theta =  Math.toRadians(360) - theta;
        }

        // Calculate the Camera's pitch from direction vector.
        double phi = (Math.acos(dirVector.normalize().dot(globalUp.normalize())) -  Math.toRadians(90))*-1;

        // Apply rotation and position
        Quaternion quartEuler = new Quaternion((float)phi, (float)theta, 0);
        cameraNode.setRotation(quartEuler);
        cameraNode.setPosition(cameraPosition);
    }
}
