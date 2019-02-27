package com.Quire3D.util.handles;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.fragments.ObjectParamsFragment;
import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.util.actions.RotateAction;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.DragListener;
import com.viro.core.Node;
import com.viro.core.Vector;
import com.viro.core.ViroView;

import java.util.List;

public class RotationHandles extends Handles {
    private Vector oldAnglePos;

    public RotationHandles(Node parent, ObjectParamsFragment paramsFragment)  {
        super(ViroActivity.getView(), "file:///android_asset/rotate_handle.obj", parent, paramsFragment);
        List<Node> handles = getHandleRoot().getChildNodes();
        handles.get(0).setRotation(new Vector(0f, Math.PI / 2, 0f));
        handles.get(1).setRotation(new Vector(Math.PI / 2, 0f, 0f));
        handles.get(2).setRotation(new Vector(0f, 0f, 0f));
        setDragListeners(handles.get(0), new Vector(1f, 0f, 0f));
        setDragListeners(handles.get(2), new Vector(0f, 0f, 1f));
        setDragListeners(handles.get(1), new Vector(0f, 1f, 0f));
    }

    private void setDragListeners(final Node handle, final Vector planeNormal) {

        handle.setDragType(Node.DragType.FIXED_TO_PLANE);
        handle.setDragPlanePoint(new Vector(0f, 0f, 0f));
        handle.setDragPlaneNormal(planeNormal);
        handle.setDragListener(new DragListener() {
            @Override
            public void onDrag(int i, Node node, Vector local, Vector world) {
                Vector diff;
                if(planeNormal.x == 1) {
                    diff = new Vector(Math.atan2(oldAnglePos.y, oldAnglePos.z) - Math.atan2(world.y, world.z), 0f, 0f);
                } else if(planeNormal.y == 1) {
                    diff = new Vector(0f, Math.atan2(oldAnglePos.z, oldAnglePos.x) - Math.atan2(world.z, world.x), 0f);
                } else {
                    diff = new Vector(0f,  0f, Math.atan2(oldAnglePos.x, oldAnglePos.y) - Math.atan2(world.x, world.y));
                }

                Vector rot = parent.getRotationEulerRealtime();
                parent.setRotation(diff.add(rot));
                oldAnglePos = world;
                node.setPosition(new Vector(0f, 0f, 0f));
            }
        });

        handle.setClickListener(new ClickListener() {
            Vector oldPos;

            @Override
            public void onClick(int i, Node node, Vector vector) {

            }

            @Override
            public void onClickState(int i, Node node, ClickState clickState, Vector vector) {
                if(clickState.equals(ClickState.CLICK_DOWN)){
                    oldAnglePos = vector;
                    oldPos = parent.getRotationEulerRealtime();
                    ViroActivity.getCamera().setLock(true);
                }else if(clickState.equals(ClickState.CLICK_UP)) {
                    node.setPosition(new Vector(0f, 0f, 0f));

                    ActionsController.getInstance().addAction(new RotateAction(parent, oldPos, parent.getRotationEulerRealtime()));
                    ViroActivity.getCamera().setLock(false);

                    paramsFrag.update(parent);
                }
            }
        });
    }
}
