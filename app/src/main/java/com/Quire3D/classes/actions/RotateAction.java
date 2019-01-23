package com.Quire3D.classes.actions;

import com.viro.core.Node;
import com.viro.core.Vector;

public class RotateAction extends Action {
    private Vector prevRotation;
    private Vector newRotation;

    public RotateAction(Node node, Vector prevPosition, Vector newPosition) {
        super(node);
        this.prevRotation = prevPosition;
        this.newRotation = newPosition;
    }

    @Override
    public void executeUndo() {
        node.setRotation(prevRotation);
    }

    @Override
    public void executeRedo() {
        node.setRotation(newRotation);
    }
}
