package com.Quire3D.classes.actions;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.fragments.CreatePrimitiveFragment;
import com.viro.core.Geometry;
import com.viro.core.Node;

public class CreateAction extends Action {
    private Geometry geometry;
    private String name;

    public CreateAction(Node node, Geometry geometry, String name) {
        super(node, "create");
        this.geometry = geometry;
        this.name = name;
    }

    @Override
    public void execute(boolean isUndo) {
        if(isUndo) {
            node.disposeAll(true);
        } else {
            Node n = new Node();
            n.setGeometry(geometry);
            n.setName(name);

            ViroActivity.makeNodeSelectable(n);
            ViroActivity.getScene().getRootNode().addChildNode(n);
        }
    }
}
