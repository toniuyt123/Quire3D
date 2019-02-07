package com.Quire3D.util.actions;

import com.viro.core.Node;
import com.viro.core.Vector;

public class ScaleAction extends Action {
    private Vector prevScale;
    private Vector newScale;
    private Node node;

    public ScaleAction(Node node, Vector prevPosition, Vector newPosition) {
        this.node = node;
        this.prevScale = prevPosition;
        this.newScale = newPosition;
    }

    @Override
    public void executeUndo() {
        node.setScale(prevScale);
    }

    @Override
    public void executeRedo() {
        node.setScale(newScale);
    }
}
