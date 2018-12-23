package com.Quire3D.classes.actions;

import com.Quire3D.fragments.CreatePrimitiveFragment;
import com.viro.core.Geometry;
import com.viro.core.Node;

public class CreateAction extends Action {
    private Geometry geometry;
    private String name = "";

    public CreateAction(Node node, String name) {
        super(node, "create");
        this.name = name;
    }

    public void execute(boolean isUndo) {
        if(isUndo) {
            node.disposeAll(true);
        } else {
            if(name.contains("cube")) {
                geometry = CreatePrimitiveFragment.createCube();
            } else if(name.contains("sphere")) {
                geometry = CreatePrimitiveFragment.createSphere();
            } else if(name.contains("quad")) {
                geometry = CreatePrimitiveFragment.createQuad();
            }

            CreatePrimitiveFragment.addToScene(geometry, name, false);
        }
    }
}
