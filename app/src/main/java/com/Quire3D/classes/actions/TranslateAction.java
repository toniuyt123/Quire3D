package com.Quire3D.classes.actions;

import com.viro.core.Node;
import com.viro.core.Vector;

public class TranslateAction extends Action {
    private Vector location;

    public TranslateAction(Node node, Vector location) {
        super(node, "translate");
        this.location = location;
    }

    @Override
    public void execute(boolean isUndo) {
        node.setPosition(location);
    }
}
