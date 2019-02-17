package com.Quire3D.util.handles;

import android.util.Log;

import com.Quire3D.activities.ViroActivity;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.DragListener;
import com.viro.core.Node;
import com.viro.core.Vector;
import com.viro.core.ViroView;

import java.util.List;

public class ScaleHandles extends Handles {
    private float startDistance;

    public ScaleHandles(Node parent/*, Fragment paramsFragment*/)  {
        super(ViroActivity.getView(), "file:///android_asset/scale_handle.obj", parent/*, paramsFragment*/);
        List<Node> handles = getHandleRoot().getChildNodes();
        setDragListeners(handles.get(0), new Vector(0f, 1f, 0f), new Vector(1f, 0f, 0f));
        setDragListeners(handles.get(1), new Vector(0f, 0f, 1f), new Vector(0f, 1f, 0f));
        setDragListeners(handles.get(2), new Vector(0f, 1f, 0f), new Vector(0f, 0f, 1f));
    }

    private void setDragListeners(final Node handle, Vector planeNormal, final Vector lineToDrag) {

        handle.setDragType(Node.DragType.FIXED_TO_PLANE);
        handle.setDragPlanePoint(new Vector(0f, 0f, 0f));
        handle.setDragPlaneNormal(planeNormal);
        handle.setDragListener(new DragListener() {
            @Override
            public void onDrag(int i, Node node, Vector local, Vector world) {
                Vector oldScale = parent.getScaleRealtime();
                Log.i("scalee", oldScale.toString());
                float newScale = world.x - oldScale.x;
                Log.i("scalee", "a" + newScale);
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
}
