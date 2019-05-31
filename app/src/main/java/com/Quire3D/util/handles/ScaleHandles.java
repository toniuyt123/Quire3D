package com.Quire3D.util.handles;

import android.util.Log;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.fragments.ObjectParamsFragment;
import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.util.actions.ScaleAction;
import com.Quire3D.util.actions.TranslateAction;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.DragListener;
import com.viro.core.Node;
import com.viro.core.Vector;
import com.viro.core.ViroView;

import java.util.List;

public class ScaleHandles extends Handles {
    private Vector startScale;

    public ScaleHandles(Node parent, ObjectParamsFragment paramsFragment)  {
        super(ViroActivity.getView(), "file:///android_asset/scale_handle.obj", parent, paramsFragment);
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
                Vector oldScale = parent.getScaleRealtime();
                Vector newScale = handleRoot.convertWorldPositionToLocalSpace(world).add(startScale);
                if(lineToDrag.x == 1) {
                    oldScale.x = newScale.x * handleRoot.getScaleRealtime().x;
                } else if(lineToDrag.y == 1) {
                    oldScale.y = newScale.y * handleRoot.getScaleRealtime().y;
                } else {
                    oldScale.z = newScale.z * handleRoot.getScaleRealtime().z;
                }
                parent.setScale(oldScale);
            }
        });

        handle.setClickListener(new ClickListener() {
            @Override
            public void onClick(int i, Node node, Vector vector) {

            }

            @Override
            public void onClickState(int i, Node node, ClickState clickState, Vector vector) {
                if(clickState.equals(ClickState.CLICK_DOWN)) {
                    startScale = parent.getScaleRealtime();
                    ViroActivity.getCamera().setLock(true);
                }else if(clickState.equals(ClickState.CLICK_UP)) {
                    Vector newScale = parent.getScaleRealtime();
                    node.setPosition(new Vector(0f, 0f, 0f));
                    ActionsController.getInstance().addAction(new ScaleAction(parent, startScale, newScale));
                    ViroActivity.getCamera().setLock(false);

                    paramsFrag.update(parent);
                }
            }
        });
    }
}
