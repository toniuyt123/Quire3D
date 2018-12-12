package com.Quire3D.classes.actions;

import com.viro.core.Geometry;
import com.viro.core.Node;

public class CreateAction extends Action {
    private Geometry geometry;

    public CreateAction(Node node, Geometry geometry) {
        super(node, "create");
        this.geometry = geometry;
    }

    @Override
    public void execute(boolean isUndo) {
        if(isUndo) {
            node.dispose();
        }
    }
}
