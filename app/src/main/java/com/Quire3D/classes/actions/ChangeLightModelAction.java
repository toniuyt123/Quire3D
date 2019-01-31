package com.Quire3D.classes.actions;

import com.viro.core.Material;
import com.viro.core.Node;

public class ChangeLightModelAction extends Action {
    private Material.LightingModel prevModel;
    private Material.LightingModel newModel;
    private Material mat;

    public ChangeLightModelAction(Material.LightingModel prevModel, Material.LightingModel newModel, Material mat) {
        this.prevModel = prevModel;
        this.newModel = newModel;
        this.mat = mat;
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
