package com.Quire3D.classes.actions;

import com.viro.core.Material;
import com.viro.core.Node;

public class ChangeLightModelAction extends Action {
    private Material.LightingModel prevModel;
    private Material.LightingModel newModel;
    private Material mat;

    public ChangeLightModelAction(Node node, Material.LightingModel prevModel, Material.LightingModel newModel) {
        super(node);
        this.prevModel = prevModel;
        this.newModel = newModel;
        this.mat = node.getGeometry().getMaterials().get(0);
    }

    @Override
    public void executeUndo() {
        mat.setLightingModel(prevModel);
    }

    @Override
    public void executeRedo() {
        mat.setLightingModel(newModel);
    }
}
