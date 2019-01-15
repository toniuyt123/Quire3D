package com.Quire3D.classes.actions;

import com.viro.core.Node;
import com.viro.core.Vector;

public class ScaleAction extends Action {
    private Vector prevScale;
    private Vector newScale;

    public ScaleAction(Node node, Vector prevPosition, Vector newPosition) {
        super(node, "translate");
        this.prevScale = prevPosition;
        this.newScale = newPosition;
    }

    @Override
    public void execute(boolean isUndo) {
        if(isUndo) {
            node.setScale(prevScale);
        } else {
            node.setScale(newScale);
        }
    }
}
