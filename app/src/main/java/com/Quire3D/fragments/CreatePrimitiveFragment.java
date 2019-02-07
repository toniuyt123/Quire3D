package com.Quire3D.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.util.actions.CreateAction;
import com.Quire3D.virosample.R;
import com.viro.core.Box;
import com.viro.core.Geometry;
import com.viro.core.Node;
import com.viro.core.Quad;
import com.viro.core.Sphere;

import java.util.Arrays;

public class CreatePrimitiveFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_primitive, container, false);

        Button createCube = view.findViewById(R.id.createCube);
        createCube.setOnClickListener(this);
        Button createSphere = view.findViewById(R.id.createSphere);
        createSphere.setOnClickListener(this);
        Button createQuad = view.findViewById(R.id.createQuad);
        createQuad.setOnClickListener(this);
        Button deletebutton = view.findViewById(R.id.delete);
        deletebutton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        Geometry obj = new Geometry();
        String name = "";
        switch (view.getId()) {
            case R.id.createCube:
                obj = new Box(1f,1f,1f);
                name = "cube";
                break;
            case R.id.createSphere:
                obj = new Sphere(1f);;
                name = "sphere";
                break;
            case R.id.createQuad:
                obj = new Quad(1f, 1f);;
                name = "quad";
                break;
            case R.id.delete:
                Node selected = ViroActivity.getSelectedNode();
                HierarchyFragment hierarchy = (HierarchyFragment) getActivity().getFragmentManager().findFragmentById(R.id.hierarchyFragment);
                hierarchy.removeFromHierarchy(selected);
                if(selected != null) {
                    selected.disposeAll(true);
                    ViroActivity.setActiveHandles(null);
                }
                return;
        }
        addToScene(obj, name, true);
    }

    public void addToScene(Geometry geometry, String name, boolean recordAction) {
        geometry.setMaterials(Arrays.asList(MaterialsFragment.getMaterials().get(0)));
        Node n = new Node();
        n.setGeometry(geometry);
        n.setName(name);

        ViroActivity activity = (ViroActivity) getActivity();
        activity.makeNodeSelectable(n);
        ViroActivity.getScene().getRootNode().addChildNode(n);

        if(recordAction) {
            ActionsController.getInstance().addAction(new CreateAction(n, name));
        }

        FragmentManager fm =  activity.getFragmentManager();
        HierarchyFragment hierarchy = (HierarchyFragment) fm.findFragmentById(R.id.hierarchyFragment);
        hierarchy.addToHierarchy(n, 0);
    }
}
