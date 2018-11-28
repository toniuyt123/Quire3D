package com.Quire3D.classes;

import android.util.Log;

import com.viro.core.Node;
import com.viro.core.Vector;

import java.util.ArrayList;
import java.util.List;

public class Action<T> {
    private Node node;
    private String action;
    private ArrayList<T> args;

    public Action(Node node, String action, List<T> args) {
        this.node = node;
        this.action = action;
        this.args = new ArrayList<>(args);
    }

    public void execute() {
        switch(action) {
            case "p":
                node.setPosition((Vector) args.get(0));
                break;
        }
    }
}
