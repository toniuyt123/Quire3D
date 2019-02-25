package com.Quire3D.util.handles;


import android.util.Log;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.util.actions.TranslateAction;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.DragListener;
import com.viro.core.Node;
import com.viro.core.Vector;
import com.viro.core.ViroView;

import java.util.List;

public class TranslateHandles extends Handles {

    public TranslateHandles(Node parent/*, Fragment paramsFragment*/)  {
        super(ViroActivity.getView(), "file:///android_asset/translate_handle.obj", parent/*, paramsFragment*/);
        List<Node> handles = getHandleRoot().getChildNodes();
        setDragListeners(handles.get(0), new Vector(0f, 1f, 0f), new Vector(1f, 0f, 0f));
        setDragListeners(handles.get(1), new Vector(0f, 0f, 1f), new Vector(0f, 1f, 0f));
        setDragListeners(handles.get(2), new Vector(0f, 1f, 0f), new Vector(0f, 0f, 1f));
        handleRoot.setScale(new Vector(1.5f, 1.5f, 1.5f));
    }

    private void setDragListeners(final Node handle, Vector planeNormal, final Vector lineToDrag) {

        handle.setDragType(Node.DragType.FIXED_TO_PLANE);
        handle.setDragPlanePoint(new Vector(0f, 0f, 0f));
        handle.setDragPlaneNormal(planeNormal);
        handle.setDragListener(new DragListener() {
            @Override
            public void onDrag(int i, Node node, Vector local, Vector world) {
                Vector oldPos = handleRoot.getPositionRealtime();
                Vector newPos;
                if(lineToDrag.x == 1f) {
                    newPos = new Vector(world.x, oldPos.y, oldPos.z);
                } else if(lineToDrag.y == 1f) {
                    newPos = new Vector(oldPos.x, world.y, oldPos.z);
                } else {
                    newPos = new Vector(oldPos.x, oldPos.y, world.z);
                }
                parent.setPosition(parent.getParentNode().convertWorldPositionToLocalSpace(newPos));
                handleRoot.setPosition(newPos);
                node.setPosition(new Vector(0f, 0f, 0f));

                //paramsFrag.updatePositionText(parent, handle.getName().charAt(0));
            }
        });

        handle.setClickListener(new ClickListener() {
            private Vector oldPos;
            private boolean lockStarted = false;

            @Override
            public void onClick(int i, Node node, Vector vector) {

            }

            @Override
            public void onClickState(int i, Node node, ClickState clickState, Vector vector) {
                if(clickState.equals(ClickState.CLICK_DOWN)) {
                    oldPos = parent.getWorldTransformRealTime().extractTranslation();
                    ViroActivity.getCamera().toggleLock();
                    lockStarted = true;
                }else if(clickState.equals(ClickState.CLICK_UP)) {
                    Vector newPos = parent.getWorldTransformRealTime().extractTranslation();
                    getHandleRoot().setPosition(newPos);
                    node.setPosition(new Vector(0f, 0f, 0f));
                    ActionsController.getInstance().addAction(new TranslateAction(parent, oldPos, newPos));

                    if(lockStarted) {
                        ViroActivity.getCamera().toggleLock();
                        lockStarted = false;
                    }
                }
            }
        });
    }
}
