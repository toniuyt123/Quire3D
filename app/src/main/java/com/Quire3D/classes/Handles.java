package com.Quire3D.classes;

import android.app.Fragment;
import android.net.Uri;
import android.util.Log;
import android.graphics.Color;

import com.Quire3D.fragments.ObjectParamsFragment;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Vector;
import com.viro.core.ViroView;

import java.util.Arrays;

public class Handles {
    protected Node parent;
    private Node handleRoot;
    protected ObjectParamsFragment paramsFrag;

    public Handles(ViroView view, String handleAssetPath, Node parent, Fragment paramsFrag) {
        handleRoot = new Node();
        handleRoot.setName("Handles");
        Object3D xHandle = new Object3D();
        xHandle.setName("x");
        Object3D yHandle = new Object3D();
        yHandle.setName("y");
        Object3D zHandle = new Object3D();
        zHandle.setName("z");
        this.parent = parent;
        this.parent.addChildNode(handleRoot);
        this.paramsFrag = (ObjectParamsFragment) paramsFrag;

        initHandle(xHandle, view, handleAssetPath, new Vector(0f, 0f, -Math.PI / 2), Color.RED);
        initHandle(yHandle, view, handleAssetPath, new Vector(0f, 0f, 0), Color.GREEN);
        initHandle(zHandle, view, handleAssetPath, new Vector(-Math.PI / 2, 0, 0), Color.BLUE);

        //setDragListeners(xHandle, new Vector(0f, 1f, 0f), new Vector(1f, 0f, 0f));
        //setDragListeners(yHandle, new Vector(0f, 0f, 1f), new Vector(0f, 1f, 0f));
        //setDragListeners(zHandle, new Vector(0f, 1f, 0f), new Vector(0f, 0f, 1f));


    }

    private void initHandle(final Object3D handle, ViroView view, String handleAssetPath, final Vector rotation, final int color) {
        handle.loadModel(view.getViroContext(), Uri.parse(handleAssetPath), Object3D.Type.OBJ, new AsyncObject3DListener() {
            public void onObject3DFailed(String error) {
                Log.w("viro", "Failed to load the model");
            }
            public void onObject3DLoaded(Object3D object, Object3D.Type type){
                Log.i("viro", "Successfully loaded the model!");

                Material handleMaterial = new Material();
                handleMaterial.setBlendMode(Material.BlendMode.NONE);
                handleMaterial.setLightingModel(Material.LightingModel.CONSTANT);
                handleMaterial.setDiffuseColor(color);

                handle.getGeometry().setMaterials(Arrays.asList(handleMaterial));
                handle.setRotation(rotation);
            }
        });

        handleRoot.addChildNode(handle);
    }


    public Node getHandleRoot() {
        return handleRoot;
    }
}
