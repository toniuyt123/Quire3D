package com.Quire3D.util.actions;

import com.viro.core.Node;
import com.viro.core.Vector;

public class RotateAction extends Action {
    private Vector prevRotation;
    private Vector newRotation;
    private Node node;

    public RotateAction(Node node, Vector prevPosition, Vector newPosition) {
        this.node = node;
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
