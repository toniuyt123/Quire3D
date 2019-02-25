package com.Quire3D.util.actions;

import com.Quire3D.activities.ViroActivity;
import com.viro.core.Node;
import com.viro.core.Vector;

public class TranslateAction extends Action {
    private Vector prevPosition;
    private Vector newPosition;
    private Node node;

    public TranslateAction(Node node, Vector prevPosition, Vector newPosition) {
        this.node = node;
        this.prevPosition = prevPosition;
        this.newPosition = newPosition;
    }

    @Override
    public void executeUndo() {
       node.setPosition(node.getParentNode().convertWorldPositionToLocalSpace(prevPosition));
       moveHandles(prevPosition);
    }

    @Override
    public void executeRedo() {
       node.setPosition(node.getParentNode().convertWorldPositionToLocalSpace(newPosition));
       moveHandles(newPosition);
    }

    private void moveHandles(Vector pos){
        if(ViroActivity.getActiveHandles() != null) {
            ViroActivity.getActiveHandles().getHandleRoot().setPosition(pos);
        }
    }
}
