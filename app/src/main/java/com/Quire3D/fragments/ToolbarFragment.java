package com.Quire3D.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.util.handles.RotationHandles;
import com.Quire3D.util.handles.ScaleHandles;
import com.Quire3D.util.handles.TranslateHandles;
import com.Quire3D.virosample.R;
import com.viro.core.Node;
import com.viro.core.Vector;
import com.viro.core.ViroView;

import java.util.ArrayList;


public class ToolbarFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_toolbar, container, false);

        ImageButton translate = view.findViewById(R.id.Translate);
        translate.setOnClickListener(this);
        ImageButton scale = view.findViewById(R.id.Scale);
        scale.setOnClickListener(this);
        ImageButton rotate = view.findViewById(R.id.Rotate);
        rotate.setOnClickListener(this);
        //TextView duplicate = view.findViewById(R.id.Duplicate);
        //duplicate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        ViroView viroView = ViroActivity.getView();
        Node selected = ViroActivity.getSelectedNode();
        ObjectParamsFragment paramFrag = (ObjectParamsFragment) getFragmentManager().findFragmentById(SwitchViewFragment.getCurrentId());
        if(selected != null) {
            switch (view.getId()) {
                case R.id.Translate:
                    ViroActivity.setDefaultHandle('t');
                    ViroActivity.changeHandles(new TranslateHandles(selected, paramFrag)); break;
                case R.id.Scale:
                    ViroActivity.setDefaultHandle('s');
                    ViroActivity.changeHandles(new ScaleHandles(selected, paramFrag)); break;
                case R.id.Rotate:
                    ViroActivity.setDefaultHandle('r');
                    ViroActivity.changeHandles(new RotationHandles(selected, paramFrag));break;
                /*case R.id.Duplicate:
                    duplicateNode(selected, selected.getWorldTransformRealTime().extractTranslation());
                    break;*/
            }
        }
    }

    public void duplicateNode(Node node, Vector newPosition) {
        Node.NodeBuilder builder = new Node.NodeBuilder<>();
        builder.name(node.getName())
            .geometry(node.getGeometry())
            .clickListener(node.getClickListener())
            .children((ArrayList<Node>) node.getChildNodes())
            .scale(node.getScaleRealtime())
            .position(node.getWorldTransformRealTime().extractTranslation())
            .visible(true);

        Node newNode = builder.build();
        node.getParentNode().addChildNode(newNode);
        newNode.setPosition(newPosition);

        FragmentManager fm =  getActivity().getFragmentManager();
        HierarchyFragment hierarchy = (HierarchyFragment) fm.findFragmentById(R.id.hierarchyFragment);
        hierarchy.addToHierarchy(newNode, hierarchy.getNodeLevel(node));
    }
}
