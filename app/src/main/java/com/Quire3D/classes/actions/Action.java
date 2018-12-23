package com.Quire3D.classes.actions;

import com.viro.core.Node;

public abstract class Action<T> {
    protected Node node;
    private String name;

    public Action(Node node, String name) {
        this.node = node;
        this.name = name;
    }

    public abstract void execute(boolean isUndo);

    public void execute() {
        execute(true);
    }

    public String getName() {
        return name;
    }
}
