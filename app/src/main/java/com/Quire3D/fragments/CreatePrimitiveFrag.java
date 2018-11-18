package com.Quire3D.fragments;

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
import com.viro.core.Node;
import com.viro.core.ViroView;

public class CreatePrimitiveFrag extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_primitive, container, false);

        ImageButton createCube = view.findViewById(R.id.createCube);
        createCube.setOnClickListener(this);

        view.bringToFront();
        return view;
    }

    public void createCube() {
        Geometry cube = new Box(1f,1f,1f);
        Node cubeNode = new Node();
        cubeNode.setGeometry(cube);
        ViroActivity.getScene().getRootNode().addChildNode(cubeNode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createCube:
                createCube();
                break;
        }
    }
}
