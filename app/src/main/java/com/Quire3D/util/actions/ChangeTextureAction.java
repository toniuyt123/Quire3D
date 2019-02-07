package com.Quire3D.util.actions;

import com.viro.core.Material;
import com.viro.core.Texture;

public class ChangeTextureAction extends Action {
    private Texture prevTexture;
    private Texture newTexture;
    private Material mat;

    public ChangeTextureAction(Texture prevTexture, Texture newTexture, Material mat) {
        this.prevTexture = prevTexture;
        this.newTexture = newTexture;
        this.mat = mat;
    }

    @Override
    public void executeUndo() {
        mat.setDiffuseTexture(prevTexture);
    }

    @Override
    public void executeRedo() {
        mat.setDiffuseTexture(newTexture);
    }
}
