package com.Quire3D.fragments;

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
import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.util.actions.RotateAction;
import com.Quire3D.util.actions.TranslateAction;
import com.Quire3D.util.actions.ScaleAction;
import com.Quire3D.virosample.R;
import com.viro.core.Node;
import com.viro.core.Vector;

import java.util.Locale;


public class PositionalDataFragment extends ObjectParamsFragment{
    private EditText xPos, yPos, zPos;
    private EditText xScale, yScale, zScale;
    private EditText xRot, yRot, zRot;
    private boolean stopWatchers = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_positional_data, container, false);

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

    @Override
    public void update(Node selected) {
        stopWatchers = true;
        updatePositionText(selected);
        updateScaleText(selected);
        updateRotationText(selected);
        stopWatchers = false;
    }

    public void updatePositionText(Node node) {
        Vector pos = node.getPositionRealtime();
        xPos.setText(String.format(Locale.US, "%.3f", pos.x));
        yPos.setText(String.format(Locale.US, "%.3f", pos.y));
        zPos.setText(String.format(Locale.US, "%.3f", pos.z));
    }

    public void updateScaleText(Node node) {
        Vector scale = node.getScaleRealtime();
        xScale.setText(String.format(Locale.US, "%.3f", scale.x));
        yScale.setText(String.format(Locale.US, "%.3f", scale.y));
        zScale.setText(String.format(Locale.US, "%.3f", scale.z));
    }

    public void updateRotationText(Node node) {
        Vector rotation = node.getRotationEulerRealtime();
        xRot.setText(String.format(Locale.US, "%.3f", rotation.x));
        yRot.setText(String.format(Locale.US, "%.3f", rotation.y));
        zRot.setText(String.format(Locale.US, "%.3f", rotation.z));
    }

    class PosTextWatcher implements TextWatcher {
        int[] axis;

        PosTextWatcher(int[] axis) {
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
            if(!stopWatchers) {
                try {
                    Node selected = ViroActivity.getSelectedNode();
                    Vector currentPosition = selected.getPositionRealtime();
                    Vector newPosition = getNewValue(currentPosition, Float.parseFloat(s.toString()), axis);
                    selected.setPosition(selected.getParentNode().convertWorldPositionToLocalSpace(newPosition));

                    if(ViroActivity.getActiveHandles() != null) {
                        ViroActivity.getActiveHandles().getHandleRoot().setPosition(newPosition);
                    }


                    ActionsController.getInstance().addAction(new TranslateAction(selected, currentPosition, selected.getPositionRealtime()));
                } catch (Exception e) {
                    Log.e("parseError", e.getMessage());
                }
            }
        }
    }

    class ScaleTextWatcher implements TextWatcher {
        int[] axis;

        ScaleTextWatcher(int[] axis) {
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
            if(!stopWatchers) {
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
    }

    class RotationTextWatcher implements TextWatcher {
        int[] axis;

        RotationTextWatcher(int[] axis) {
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
            if(!stopWatchers) {
                try {
                    Node selected = ViroActivity.getSelectedNode();
                    Vector rot = selected.getRotationEulerRealtime();
                    Vector newRot = getNewValue(rot, Float.parseFloat(s.toString()), axis);
                    newRot.x = (float)Math.toRadians(newRot.x);
                    newRot.y = (float)Math.toRadians(newRot.y);
                    newRot.z = (float)Math.toRadians(newRot.z);

                    selected.setRotation(newRot);

                    ActionsController.getInstance().addAction(new RotateAction(selected, rot, selected.getRotationEulerRealtime()));
                } catch (Exception e) {
                    Log.e("parseError", e.getMessage());
                }
            }
        }
    }

    protected Vector getNewValue(Vector old, float value, int[] axis) {
        Vector diff = new Vector((value - old.x) * axis[0], (value - old.y) * axis[1],(value - old.z) * axis[2]);
        Log.i("heck" ,diff.toString());
        return old.add(diff);
    }
}
