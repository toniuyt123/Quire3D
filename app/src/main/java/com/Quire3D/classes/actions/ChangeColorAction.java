package com.Quire3D.classes.actions;

import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Vector;

public class ChangeColorAction extends Action {
    private int prevColor;
    private int newColor;
    private Material mat;

    public ChangeColorAction(Node node, int prevColor, int newColor) {
        super(node);
        this.prevColor = prevColor;
        this.newColor = newColor;
        this.mat = node.getGeometry().getMaterials().get(0);
    }

    @Override
    public void executeUndo() {
        mat.setDiffuseColor(prevColor);
    }

    @Override
    public void executeRedo() {
        mat.setDiffuseColor(newColor);
    }
}
