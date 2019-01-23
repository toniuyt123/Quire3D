package com.Quire3D.classes.actions;

import com.viro.core.Node;

public abstract class Action {
    protected Node node;

    Action(Node node) {
        this.node = node;
    }

    public abstract void executeUndo();
    public abstract void executeRedo();

}
