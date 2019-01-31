package com.Quire3D.classes.actions;

import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Vector;

public class ChangeColorAction extends Action {
    private int prevColor;
    private int newColor;
    private Material mat;

    public ChangeColorAction(int prevColor, int newColor, Material mat) {
        this.prevColor = prevColor;
        this.newColor = newColor;
        this.mat = mat;
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
