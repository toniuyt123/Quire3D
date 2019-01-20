package com.Quire3D.classes.actions;

import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Vector;

public class ChangeColorAction extends Action {
    private int prevColor;
    private int newColor;

    public ChangeColorAction(Node node, int prevColor, int newColor) {
        super(node, "translate");
        this.prevColor = prevColor;
        this.newColor = newColor;
    }

    @Override
    public void execute(boolean isUndo) {
        Material mat = node.getGeometry().getMaterials().get(0);
        if(isUndo) {
            mat.setDiffuseColor(prevColor);
        } else {
            mat.setDiffuseColor(newColor);
        }
    }
}
