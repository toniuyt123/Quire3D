package com.Quire3D.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.virosample.R;
import com.viro.core.Node;

import java.util.ArrayList;
import java.util.Arrays;


public class HierarchyFragment extends Fragment implements View.OnClickListener {
    private static LinearLayout hierarchy;
    private static final ArrayList<String> hidden = new ArrayList<>(Arrays.asList("Handles", "floor_grid"));
    private static Node root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_hierarchy, container, false);

        hierarchy = view.findViewById(R.id.Hierarchy);
        root = ViroActivity.getScene().getRootNode();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
    }

    public static void updateHierarchy() {
        Context context = ViroActivity.getView().getContext();

        expandChildren(context, root, 0);
    }

    private static void expandChildren(Context context, Node node, int level) {
        for(Node n : node.getChildNodes()){
            if(!hidden.contains(n.getName())) {
                TextView name = new TextView(context);
                String tabs = new String(new char[level]).replace("\0", "->");

                name.setText(String.format("%S%s%s", "| ", tabs, n.getName()));
                hierarchy.addView(name);
                if(n.getChildNodes().size() > 0) {
                    expandChildren(context, n, level++);
                }
            }
        }
    }

    private static Node getNodeByName(String name) {
        for(Node n : root.getChildNodes()){
            if(n.getName().equals(name)){
                return n;
            }
        }
        return null;
    }
}
