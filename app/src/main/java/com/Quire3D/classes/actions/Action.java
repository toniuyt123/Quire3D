package com.Quire3D.classes.actions;

import com.viro.core.Node;

public abstract class Action<T> {
    protected Node node;
    private String action;

    public Action(Node node, String action) {
        this.node = node;
        this.action = action;
    }

    public abstract void execute(boolean isUndo);

    public void execute() {
        execute(true);
    }
}
