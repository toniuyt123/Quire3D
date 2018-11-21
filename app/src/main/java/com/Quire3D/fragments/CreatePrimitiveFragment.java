package com.Quire3D.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.virosample.R;
import com.viro.core.Box;
import com.viro.core.Geometry;
import com.viro.core.Material;
import com.viro.core.Node;

import java.util.Arrays;

public class CreatePrimitiveFragment extends Fragment implements View.OnClickListener {
    private Material defaultMat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_primitive, container, false);

        ImageButton createCube = view.findViewById(R.id.createCube);
        createCube.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        defaultMat = new Material();
        defaultMat.setDiffuseColor(Color.WHITE);
        defaultMat.setLightingModel(Material.LightingModel.LAMBERT);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createCube:
                createCube();
                break;
        }
    }

    public void createCube() {
        Geometry cube = new Box(1f,1f,1f);
        cube.setMaterials(Arrays.asList(defaultMat));
        Node cubeNode = new Node();
        cubeNode.setGeometry(cube);
        ViroActivity.makeNodeSelectable(cubeNode);

        ViroActivity.getScene().getRootNode().addChildNode(cubeNode);
    }
}
