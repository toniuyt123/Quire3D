package com.Quire3D.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
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
import java.util.HashMap;
import java.util.Map;


public class HierarchyFragment extends Fragment implements View.OnClickListener {
    private static final ArrayList<String> hidden = new ArrayList<>(Arrays.asList("Handles", "floor_grid"));
    private static LinearLayout hierarchy;
    private static HashMap<TextView, Node> nodes = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_hierarchy, container, false);

        hierarchy = view.findViewById(R.id.Hierarchy);
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
        expandChildren(ViroActivity.getScene().getRootNode(), 0);
    }

    private static void expandChildren(Node node, int level) {
        for(Node n : node.getChildNodes()){
            if(!hidden.contains(n.getName())) {
                addToHierarchy(n, level);
                if(n.getChildNodes().size() > 0) {
                    expandChildren(n, level + 1);
                }
            }
        }
    }

    private static Node getNodeByName(String name) {
        for(TextView t: nodes.keySet()){
            if(t.getText().equals(name)) {
                return nodes.get(t);
            }
        }
        return null;
    }

    public static void addToHierarchy(Node node, int level){
        TextView name = new TextView(ViroActivity.getView().getContext());
        String tabs = new String(new char[level]).replace("\0", "->");

        name.setText(String.format("%S%s%s", "| ", tabs, node.getName()));
        hierarchy.addView(name);

        nodes.put(name, node);
    }

    public static void removeFromHierarchy(Node node) {
        for(Map.Entry entry: nodes.entrySet()){
            Node n = (Node)entry.getValue();
            if(entry.getValue().equals(node)){
                TextView key = (TextView) entry.getKey();
                hierarchy.removeView(key);
                nodes.remove(key);
                return;
            }
        }
    }

    public static ArrayList<Node> getExportableObjects(Node root) {
        /*ArrayList<Node> objects = new ArrayList<Node>();
        for(Node n : root.getChildNodes()){
            if(!hidden.contains(n.getName())) {

                if(n.getGeometry() != null){
                    objects.add(n);
                }
                if(n.getChildNodes().size() > 0) {
                    ArrayList<Node> childs = new ArrayList<Node>(getExportableObjects(n));
                    objects.addAll(childs);
                }
            }
        }

        return objects;*/
        return new ArrayList<>(nodes.values());
    }
}
