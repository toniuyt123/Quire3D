package com.Quire3D.util.actions;

import com.viro.core.Node;

public class ChangeParentAction extends Action {
    private Node prevParent;
    private Node newParent;
    private Node node;

    public ChangeParentAction(Node prevParent, Node newParent, Node node) {
        this.prevParent = prevParent;
        this.newParent = newParent;
        this.node = node;
    }

    @Override
    public void executeUndo() {
        node.removeFromParentNode();
        prevParent.addChildNode(node);
    }

    @Override
    public void executeRedo() {
        node.removeFromParentNode();
        newParent.addChildNode(node);
    }
}
