package com.Quire3D.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.classes.ActionsController;
import com.Quire3D.classes.actions.RotateAction;
import com.Quire3D.classes.actions.TranslateAction;
import com.Quire3D.classes.actions.ScaleAction;
import com.Quire3D.virosample.R;
import com.viro.core.Node;
import com.viro.core.Vector;

import java.util.Locale;


public class ObjectParamsFragment extends Fragment{
    private EditText xPos, yPos, zPos;
    private EditText xScale, yScale, zScale;
    private EditText xRot, yRot, zRot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_object_params, container, false);

        this.xPos = view.findViewById(R.id.xPosition);
        this.yPos = view.findViewById(R.id.yPosition);
        this.zPos = view.findViewById(R.id.zPosition);
        xPos.addTextChangedListener(new PosTextWatcher(new int[]{1,0,0}));
        yPos.addTextChangedListener(new PosTextWatcher(new int[]{0,1,0}));
        zPos.addTextChangedListener(new PosTextWatcher(new int[]{0,0,1}));

        this.xScale = view.findViewById(R.id.xScale);
        this.yScale = view.findViewById(R.id.yScale);
        this.zScale = view.findViewById(R.id.zScale);
        xScale.addTextChangedListener(new ScaleTextWatcher(new int[]{1,0,0}));
        yScale.addTextChangedListener(new ScaleTextWatcher(new int[]{0,1,0}));
        zScale.addTextChangedListener(new ScaleTextWatcher(new int[]{0,0,1}));

        this.xRot = view.findViewById(R.id.xRotation);
        this.yRot = view.findViewById(R.id.yRotation);
        this.zRot = view.findViewById(R.id.zRotation);
        xRot.addTextChangedListener(new RotationTextWatcher(new int[]{1,0,0}));
        yRot.addTextChangedListener(new RotationTextWatcher(new int[]{0,1,0}));
        zRot.addTextChangedListener(new RotationTextWatcher(new int[]{0,0,1}));

        return view;
    }

    public void updatePositionText(Node node) {
        Vector pos = node.getPositionRealtime();
        xPos.setText(String.format(Locale.US, "%.3f", pos.x));
        yPos.setText(String.format(Locale.US, "%.3f", pos.y));
        zPos.setText(String.format(Locale.US, "%.3f", pos.z));
    }

    public void updateScaleText(Node node) {
        Vector scale = node.getPositionRealtime();
        xScale.setText(String.format(Locale.US, "%.3f", scale.x));
        yScale.setText(String.format(Locale.US, "%.3f", scale.y));
        zScale.setText(String.format(Locale.US, "%.3f", scale.z));
    }

    class PosTextWatcher implements TextWatcher {
        int[] axis;

        public PosTextWatcher(int[] axis) {
            this.axis = axis;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                Node selected = ViroActivity.getSelectedNode();
                Vector pos = selected.getPositionRealtime();
                selected.setPosition(getNewValue(pos, Float.parseFloat(s.toString()), axis));

                ActionsController.getInstance().addAction(new TranslateAction(selected, pos, selected.getPositionRealtime()));
            } catch (Exception e) {
                Log.e("parseError", e.getMessage());
            }
        }
    }

    class ScaleTextWatcher implements TextWatcher {
        int[] axis;

        public ScaleTextWatcher(int[] axis) {
            this.axis = axis;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                Node selected = ViroActivity.getSelectedNode();
                Vector scale = selected.getScaleRealtime();
                selected.setScale(getNewValue(scale, Float.parseFloat(s.toString()), axis));

                ActionsController.getInstance().addAction(new ScaleAction(selected, scale, selected.getScaleRealtime()));
            } catch (Exception e) {
                Log.e("parseError", e.getMessage());
            }
        }
    }

    class RotationTextWatcher implements TextWatcher {
        int[] axis;

        public RotationTextWatcher(int[] axis) {
            this.axis = axis;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                Node selected = ViroActivity.getSelectedNode();
                Vector rot = selected.getRotationEulerRealtime();
                selected.setRotation(getNewValue(rot, Float.parseFloat(s.toString()), axis));

                ActionsController.getInstance().addAction(new RotateAction(selected, rot, selected.getRotationEulerRealtime()));
            } catch (Exception e) {
                Log.e("parseError", e.getMessage());
            }
        }
    }

    protected Vector getNewValue(Vector old, float value, int[] axis) {
        Vector diff = new Vector((value - old.x) * axis[0], (value - old.y) * axis[1],(value - old.z) * axis[2]);

        return old.add(diff);
    }
}
