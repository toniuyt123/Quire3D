package com.Quire3D.classes.actions;

import com.viro.core.Node;
import com.viro.core.Vector;

public class TranslateAction extends Action {
    private Vector prevPosition;
    private Vector newPosition;

    public TranslateAction(Node node, Vector prevPosition, Vector newPosition) {
        super(node);
        this.prevPosition = prevPosition;
        this.newPosition = newPosition;
    }

    @Override
    public void executeUndo() {
       node.setPosition(prevPosition);
    }

    @Override
    public void executeRedo() {
       node.setPosition(newPosition);
    }
}
