package com.Quire3D.util.actions;

import com.viro.core.Geometry;
import com.viro.core.Node;

public class CreateAction extends Action {
    private Geometry.GeometryBuilder builder = new Geometry.GeometryBuilder();
    private String name = "";
    private Node node;

    public CreateAction(Node node, String name) {
        this.node = node;
        this.name = name;
        //builder.materials(node.getGeometry().getMaterials());
        builder.submeshes(node.getGeometry().getSubmeshes());
    }

    @Override
    public void executeUndo() {
        node.disposeAll(true);
    }

    @Override
    public void executeRedo() {
    }
}
