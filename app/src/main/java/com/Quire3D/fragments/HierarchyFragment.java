package com.Quire3D.fragments;

import android.content.ClipData;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.virosample.R;
import com.viro.core.Node;
import com.viro.core.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HierarchyFragment extends Fragment implements View.OnClickListener {
    private static final ArrayList<String> hidden = new ArrayList<>(Arrays.asList("Handles", "floor_grid"));
    private LinearLayout hierarchy;
    private DragAndDropListener dragAndDropListener = new DragAndDropListener();
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
        TextView element = (TextView) view;
        Node selection = nodes.get(element);

        ViroActivity activity = (ViroActivity) getActivity();
        activity.selectNode(selection);
    }

    public void updateHierarchy() {
        hierarchy.removeAllViews();
        expandChildren(ViroActivity.getScene().getRootNode(), 0);
    }

    private void expandChildren(Node node, int level) {
        for(Node n : node.getChildNodes()){
            if(!hidden.contains(n.getName())) {
                addToHierarchy(n, level);
                if(n.getChildNodes().size() > 0) {
                    expandChildren(n, level + 1);
                }
            }
        }
    }

    public static Node getNodeByName(String name) {
        for(TextView t: nodes.keySet()){
            if(t.getText().equals(name)) {
                return nodes.get(t);
            }
        }
        return null;
    }

    public void addToHierarchy(Node node, int level){
        TextView name = new TextView(ViroActivity.getView().getContext());
        name.setOnClickListener(this);
        name.setOnLongClickListener(dragAndDropListener);
        name.setOnDragListener(dragAndDropListener);

        name.setText(contructName(node, level));
        hierarchy.addView(name);

        nodes.put(name, node);
    }

    private String contructName(Node node, int level){
        String tabs = new String(new char[level]).replace("\0", "->");

        return String.format("%S%s%s", "| ", tabs, node.getName());
    }

    public void removeFromHierarchy(Node node) {
        List<Node> children = node.getChildNodes();
        for(int i = 0;i < children.size();i++) {
            removeFromHierarchy(children.get(i));
        }
        for(Map.Entry entry: nodes.entrySet()){
            if(entry.getValue().equals(node)){
                TextView key = (TextView) entry.getKey();
                hierarchy.removeView(key);
                nodes.remove(key);
                return;
            }
        }
    }

    public static ArrayList<Node> getExportableObjects() {
        return new ArrayList<>(nodes.values());
    }

    private class DragAndDropListener implements View.OnLongClickListener, View.OnDragListener {
        private boolean dropConsumed = false;

        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);

            view.startDrag(data, shadow, view,0);
            view.setVisibility(View.INVISIBLE);
            return true;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final TextView target = (TextView) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    TextView container = (TextView) v;
                    Log.i("dragndrop", "tekst: " + container.getText().toString());
                    int newIndex = 0;
                    int childCount = hierarchy.getChildCount();
                    for(int i = 0;i < childCount;i++) {
                        if(container.equals(hierarchy.getChildAt(i))) {
                            newIndex = i + 1;
                        }
                    }
                    hierarchy.removeView(target);
                    hierarchy.addView(target, Math.min(newIndex, childCount - 1));

                    String text = container.getText().toString();
                    String temp = text.replace("->", "");
                    int level = ((text.length() - temp.length()) / 2) + 1;

                    Node parent = nodes.get(container);
                    Node child = nodes.get(target);


                    Vector newPos = parent.convertWorldPositionToLocalSpace(child.getPositionRealtime());
                    child.removeFromParentNode();
                    parent.addChildNode(child);
                    child.setPosition(newPos
                    );

                    target.setText(contructName(child, level));
                    target.post(new Runnable(){
                        @Override
                        public void run() {
                            target.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
            }

            return true;
        }
    }
}
