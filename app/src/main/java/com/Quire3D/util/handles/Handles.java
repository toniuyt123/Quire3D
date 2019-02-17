package com.Quire3D.util.handles;

import android.net.Uri;
import android.util.Log;
import android.graphics.Color;

import com.Quire3D.activities.ViroActivity;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Scene;
import com.viro.core.Vector;
import com.viro.core.ViroView;

import java.util.Arrays;

public class Handles {
    protected Node parent;
    protected Node handleRoot;

    Handles(ViroView view, String handleAssetPath, Node parent) {
        handleRoot = new Node();
        handleRoot.setName("Handles");
        Object3D xHandle = new Object3D();
        xHandle.setName("x");
        Object3D yHandle = new Object3D();
        yHandle.setName("y");
        Object3D zHandle = new Object3D();
        zHandle.setName("z");
        this.parent = parent;
        Node rootNode = ViroActivity.getScene().getRootNode();
        rootNode.addChildNode(handleRoot);
        handleRoot.setPosition(parent.getWorldTransformRealTime().extractTranslation());


        initHandle(xHandle, view, handleAssetPath, new Vector(0f, 0f, -Math.PI / 2), Color.RED);
        initHandle(yHandle, view, handleAssetPath, new Vector(0f, 0f, 0f), Color.GREEN);
        initHandle(zHandle, view, handleAssetPath, new Vector(-Math.PI / 2, 0f, 0f), Color.BLUE);

    }

    private void initHandle(final Object3D handle, ViroView view, String handleAssetPath, final Vector rotation, final int color) {
        handleRoot.addChildNode(handle);
        handle.setRotation(rotation);
        handle.loadModel(view.getViroContext(), Uri.parse(handleAssetPath), Object3D.Type.OBJ, new AsyncObject3DListener() {
            public void onObject3DFailed(String error) {
                Log.w("viro", "Failed to load the model");
            }
            public void onObject3DLoaded(Object3D object, Object3D.Type type){
                Material handleMaterial = new Material();
                handleMaterial.setBlendMode(Material.BlendMode.NONE);
                handleMaterial.setLightingModel(Material.LightingModel.CONSTANT);
                handleMaterial.setDiffuseColor(color);

                handle.getGeometry().setMaterials(Arrays.asList(handleMaterial));
            }
        });
    }


    public Node getHandleRoot() {
        return handleRoot;
    }

    public void setParent(Node parent) {
        this.parent = parent;
        this.handleRoot.setPosition(parent.getPositionRealtime());
    }
}
