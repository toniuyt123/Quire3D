package com.Quire3D.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.Quire3D.virosample.R;
import com.viro.core.Node;
import com.viro.core.Vector;

import java.util.Locale;


public class ObjectParamsFragment extends Fragment{
    private EditText xPos, yPos, zPos;
    private EditText xRot, yRot, zRot;
    private EditText xScale, yScale, zScale;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_object_params, container, false);

        this.xPos = view.findViewById(R.id.xPosition);
        this.yPos = view.findViewById(R.id.yPosition);
        this.zPos = view.findViewById(R.id.zPosition);

        this.xRot = view.findViewById(R.id.xRotation);
        this.yRot = view.findViewById(R.id.yRotation);
        this.zRot = view.findViewById(R.id.zRotation);

        this.xScale = view.findViewById(R.id.xScale);
        this.yScale = view.findViewById(R.id.yScale);
        this.zScale = view.findViewById(R.id.zScale);

        return view;
    }

    public void updatePositionText(Node node, char axis) {
        Vector pos = node.getPositionRealtime();

        switch (axis) {
            case 'x':
                xPos.setText(String.format(Locale.US, "%.3f", pos.x)); break;
            case 'y':
                yPos.setText(String.format(Locale.US, "%.3f", pos.y)); break;
            case 'z':
                zPos.setText(String.format(Locale.US, "%.3f", pos.z)); break;
        }
    }

    public void updateScaleText(Node node, char axis) {
        Vector scale = node.getPositionRealtime();

        switch (axis) {
            case 'x':
                xScale.setText(String.format(Locale.US, "%.3f", scale.x)); break;
            case 'y':
                yScale.setText(String.format(Locale.US, "%.3f", scale.y)); break;
            case 'z':
                zScale.setText(String.format(Locale.US, "%.3f", scale.z)); break;
        }
    }
}
