package com.Quire3D.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
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
import com.viro.core.Quad;
import com.viro.core.Sphere;

import java.util.Arrays;

public class CreatePrimitiveFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_primitive, container, false);

        ImageButton createCube = view.findViewById(R.id.createCube);
        createCube.setOnClickListener(this);
        ImageButton createSphere = view.findViewById(R.id.createSphere);
        createSphere.setOnClickListener(this);
        ImageButton createQuad = view.findViewById(R.id.createQuad);
        createQuad.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createCube:
                createCube();
                break;
            case R.id.createSphere:
                createSphere();
                break;
            case R.id.createQuad:
                createQuad();
                break;
        }
    }

    public void createCube() {
        Geometry cube = new Box(1f,1f,1f);
        cube.setMaterials(Arrays.asList(makeDefaultMat()));
        addToScene(cube, "Cube");
    }

    public void createSphere() {
        Geometry sphere = new Sphere(1f);
        sphere.setMaterials(Arrays.asList(makeDefaultMat()));
        addToScene(sphere, "Sphere");
    }

    public void createQuad() {
        Geometry quad = new Quad(1f, 1f);
        Material quadMat = makeDefaultMat();
        quadMat.setCullMode(Material.CullMode.NONE);

        quad.setMaterials(Arrays.asList(quadMat));
        addToScene(quad, "Quad");
    }

    private void addToScene(Geometry geometry, String name) {
        Node n = new Node();
        n.setGeometry(geometry);
        n.setName(name);

        //ViroActivity.makeNodeSelectable(n);
        ViroActivity.getScene().getRootNode().addChildNode(n);
    }

    private Material makeDefaultMat() {
        Material defaultMat = new Material();
        defaultMat.setDiffuseColor(Color.WHITE);
        defaultMat.setLightingModel(Material.LightingModel.LAMBERT);

        return defaultMat;
    }
}
