package com.Quire3D.util;


import android.util.Log;

import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.util.actions.TranslateAction;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.DragListener;
import com.viro.core.Node;
import com.viro.core.Vector;
import com.viro.core.ViroView;

import java.util.List;

public class RotationHandles extends Handles {

    public RotationHandles(ViroView view, Node parent/*, Fragment paramsFragment*/)  {
        super(view, "file:///android_asset/rotate_handle.obj", parent/*, paramsFragment*/);
        List<Node> handles = getHandleRoot().getChildNodes();
        handles.get(0).setRotation(new Vector(0f, Math.PI / 2, 0f));
        setDragListeners(handles.get(0), new Vector(1f, 0f, 0f));
        setDragListeners(handles.get(2), new Vector(0f, 1f, 0f));
        setDragListeners(handles.get(1), new Vector(0f, 0f, 1f));
    }

    private void setDragListeners(final Node handle, final Vector planeNormal) {

        handle.setDragType(Node.DragType.FIXED_TO_PLANE);
        handle.setDragPlanePoint(new Vector(0f, 0f, 0f));
        handle.setDragPlaneNormal(planeNormal);
        handle.setDragListener(new DragListener() {
            Vector oldPos = handle.getPositionRealtime();

            @Override
            public void onDrag(int i, Node node, Vector local, Vector world) {
                double tan = 0;
                Vector diff = new Vector(0f, 0f, 0f);
                if(planeNormal.x == 1) {
                    Log.i("rotationLog", "oldpos" + oldPos.toString());
                    Log.i("rotationLog", "world" + world.toString());
                    tan = (world.y - oldPos.y) / (world.z - oldPos.z);
                    Log.i("rotationLog", "tan" + Double.toString(tan));
                    diff = new Vector(Math.atan(tan), 0f, 0f);
                    Log.i("rotationLog", diff.toString());
                }

                Vector rot = node.getRotationEulerRealtime();
                Log.i("rotationLog", rot.toString());
                parent.setRotation(diff.add(rot));
                oldPos = world;

                node.getParentNode().setRotation(new Vector(0f, 0f, 0f));
            }
        });

        handle.setClickListener(new ClickListener() {
            private Vector oldPos;

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
}
