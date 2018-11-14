package com.Quire3D.classes;

import android.net.Uri;
import android.util.Log;
import android.graphics.Color;

import com.viro.core.AsyncObject3DListener;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.DragListener;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Vector;
import com.viro.core.ViroView;

import java.util.Arrays;

public class Handles {
    private Node parent;
    private Node handleRoot;
    private Object3D xHandle;
    private Object3D yHandle;
    private Object3D zHandle;

    public Handles(ViroView view, String handleAssetPath, Node parent) {
        handleRoot = new Node();
        xHandle = new Object3D();
        yHandle = new Object3D();
        zHandle = new Object3D();
        this.parent = parent;
        this.parent.addChildNode(handleRoot);

        initHandle(xHandle, view, handleAssetPath, new Vector(0f, 0f, -Math.PI / 2), Color.RED);
        initHandle(yHandle, view, handleAssetPath, new Vector(0f, 0f, 0), Color.GREEN);
        initHandle(zHandle, view, handleAssetPath, new Vector(-Math.PI / 2, 0, 0), Color.BLUE);

        setDragListeners(xHandle, new Vector(0f, 1f, 0f), new Vector(1f, 0f, 0f));
        setDragListeners(yHandle, new Vector(0f, 0f, 1f), new Vector(0f, 1f, 0f));
        setDragListeners(zHandle, new Vector(0f, 1f, 0f), new Vector(0f, 0f, 1f));
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


    private void setDragListeners(Node handle, Vector planeNormal, final Vector lineToDrag) {
        handle.setDragType(Node.DragType.FIXED_TO_PLANE);
        handle.setDragPlanePoint(new Vector(0f, 0f, 0f));
        handle.setDragPlaneNormal(planeNormal);
        handle.setDragListener(new DragListener() {
            @Override
            public void onDrag(int i, Node node, Vector local, Vector world) {
                Vector oldPos = parent.getPositionRealtime();
                Vector newPos;
                if(lineToDrag.x == 1f) {
                    newPos = new Vector(world.x, oldPos.y, oldPos.z);
                } else if(lineToDrag.y == 1f) {
                    newPos = new Vector(oldPos.x, world.y, oldPos.z);
                } else {
                    newPos = new Vector(oldPos.x, oldPos.y, world.z);
                }
                parent.setPosition(newPos);

                node.setPosition(new Vector(0f, 0f, 0f));
            }
        });

        handle.setClickListener(new ClickListener() {
            @Override
            public void onClick(int i, Node node, Vector vector) {

            }

            @Override
            public void onClickState(int i, Node node, ClickState clickState, Vector vector) {
                if(clickState.equals(ClickState.CLICK_UP)) {
                    node.setPosition(new Vector(0f, 0f, 0f));
                }
            }
        });
    }

    public Node getHandleRoot() {
        return handleRoot;
    }
}
