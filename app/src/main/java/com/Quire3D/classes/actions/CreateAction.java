package com.Quire3D.classes.actions;

import com.Quire3D.fragments.CreatePrimitiveFragment;
import com.viro.core.Geometry;
import com.viro.core.Node;

public class CreateAction extends Action {
    private Geometry.GeometryBuilder builder = new Geometry.GeometryBuilder();
    private String name = "";

    public CreateAction(Node node, String name) {
        super(node, "create");
        this.name = name;
        //builder.materials(node.getGeometry().getMaterials());
        builder.submeshes(node.getGeometry().getSubmeshes());
    }

    public void execute(boolean isUndo) {
        if(isUndo) {
            node.disposeAll(true);
        } else {
            Geometry geometry = builder.build();

            CreatePrimitiveFragment.addToScene(geometry, name, false);
        }
    }
}
