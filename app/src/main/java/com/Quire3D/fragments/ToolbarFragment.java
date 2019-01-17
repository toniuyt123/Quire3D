package com.Quire3D.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.classes.ScaleHandles;
import com.Quire3D.classes.TranslateHandles;
import com.Quire3D.virosample.R;
import com.viro.core.Node;
import com.viro.core.ViroView;


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

        switch (view.getId()) {
            case R.id.Translate:
                ViroActivity.setDefaultHandle('t');
                ViroActivity.changeHandles(new TranslateHandles(viroView, selected)); break;
            case R.id.Scale:
                ViroActivity.setDefaultHandle('s');
                ViroActivity.changeHandles(new ScaleHandles(viroView, selected)); break;
            case R.id.Rotate:
                ViroActivity.setDefaultHandle('r'); break;
        }
    }
}
