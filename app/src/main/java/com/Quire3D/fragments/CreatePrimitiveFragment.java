package com.Quire3D.fragments;

import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.util.OBJObject;
import com.Quire3D.util.actions.Action;
import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.virosample.R;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Box;
import com.viro.core.Geometry;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Quad;
import com.viro.core.Sphere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        Button createCone = view.findViewById(R.id.createCone);
        createCone.setOnClickListener(this);
        Button createTorus = view.findViewById(R.id.createTorus);
        createTorus.setOnClickListener(this);
        Button createPyramid = view.findViewById(R.id.createPyramid);
        createPyramid.setOnClickListener(this);
        Button deletebutton = view.findViewById(R.id.Delete);
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
                obj = new Box(2f,2f,2f);
                name = "cube";
                break;
            case R.id.createSphere:
                obj = new Sphere(1f);;
                name = "sphere";
                break;
            case R.id.createQuad:
                obj = new Quad(2f, 2f);;
                name = "quad";
                break;
            case R.id.createTorus:
                loadObject("file:///android_asset/def_torus.obj");
                return;
            case R.id.createPyramid:
                loadObject("file:///android_asset/def_pyramid.obj");
                return;
            case R.id.createCone:
                loadObject("file:///android_asset/def_cone.obj");
                return;
            case R.id.Delete:
                Node selected = ViroActivity.getSelectedNode();
                ActionsController.getInstance().addAction(new DeleteAction(selected, selected.getParentNode()));
                deleteNode(selected);
                return;
        }
        addToScene(obj, name, true);
    }

    public void addToScene(Geometry geometry, String name, boolean recordAction) {
        Node n = new Node();
        n.setGeometry(geometry);

        addToScene(n, name, recordAction);
    }

    public void addToScene(Node n, String name, boolean recordAction) {
        n.getGeometry().setMaterials((Arrays.asList(MaterialsFragment.getMaterials().get(0))));
        n.setName(name);
        ViroActivity activity = (ViroActivity) getActivity();
        activity.makeNodeSelectable(n);
        ViroActivity.getScene().getRootNode().addChildNode(n);

        if(recordAction) {
            ActionsController.getInstance().addAction(new CreateAction(n));
        }

        FragmentManager fm =  activity.getFragmentManager();
        HierarchyFragment hierarchy = (HierarchyFragment) fm.findFragmentById(R.id.hierarchyFragment);
        hierarchy.addToHierarchy(n, 0);
    }

    public void deleteNode(Node n) {
        try {
            HierarchyFragment hierarchy = (HierarchyFragment) getActivity().getFragmentManager().findFragmentById(R.id.hierarchyFragment);
            hierarchy.removeFromHierarchy(n);

            n.removeFromParentNode();
            ViroActivity.getActiveHandles().getHandleRoot().disposeAll(true);
            ViroActivity.setActiveHandles(null);
            ViroActivity.setSelectedNode(null);
        } catch (NullPointerException e){
            e.getMessage();
        }
    }

    private void loadObject(String asset){
        String[] bits = asset.split("/");
        String fileName = bits[bits.length-1];
        String text = readFromAsset(fileName);
        Uri uri = Uri.parse(asset);
        OBJObject created = new OBJObject(ViroActivity.getView().getViroContext(), uri, text, this);
    }

    public String readFromAsset(String assetName){
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getContext().getAssets().open(assetName)));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            Log.d("importError", "file not found");
        }

        return text.toString();
    }

    public class CreateAction extends Action {
        private Node node;

        CreateAction(Node node) {
            this.node = node;
        }

        @Override
        public void executeUndo() {
            deleteNode(node);
        }

        @Override
        public void executeRedo() {
            ViroActivity.getScene().getRootNode().addChildNode(node);
        }
    }

    public class DeleteAction extends Action{
        private Node node;
        private Node parent;

        DeleteAction(Node node, Node parent){
            this.node = node;
            this.parent = parent;
        }

        @Override
        public void executeUndo() {
            parent.addChildNode(node);
        }

        @Override
        public void executeRedo() {
            deleteNode(node);
        }
    }
}
