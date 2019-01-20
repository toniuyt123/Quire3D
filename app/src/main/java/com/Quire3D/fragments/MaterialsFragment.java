package com.Quire3D.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.classes.ActionsController;
import com.Quire3D.classes.actions.ChangeColorAction;
import com.Quire3D.virosample.R;
import com.viro.core.Material;
import com.viro.core.Node;

import java.util.List;
import java.util.Locale;

import top.defaults.colorpicker.ColorPickerPopup;

public class MaterialsFragment extends Fragment implements View.OnClickListener {
    private View colorView;
    private TextView hexColor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_materials, container, false);

        TableRow colorPicker = view.findViewById(R.id.ColorPicker);
        colorPicker.setOnClickListener(this);
        colorView = view.findViewById(R.id.colorView);
        hexColor = view.findViewById(R.id.hexColor);

        try {
            List<Material> materials = ViroActivity.getSelectedNode().getGeometry().getMaterials();
            updateView(materials.get(0).getDiffuseColor());
        } catch (NullPointerException e) {
            updateView(Color.WHITE);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ColorPicker:
                popup();
                break;
        }
    }

    private void updateView(int color) {
        colorView.setBackgroundColor(color);
        hexColor.setText(colorHex(color));
    }

    private void popup() {
        new ColorPickerPopup.Builder(ViroActivity.getView().getContext())
                .initialColor(Color.WHITE)
                .enableAlpha(true)
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .onlyUpdateOnTouchEventUp(true)
                .build()
                .show(new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        try {
                            Node selected = ViroActivity.getSelectedNode();
                            Material material = selected.getGeometry().getMaterials().get(0);
                            ActionsController.getInstance().addAction(new ChangeColorAction(selected, material.getDiffuseColor(), color));
                            
                            material.setDiffuseColor(color);
                        } catch (NullPointerException e) {
                            e.getMessage();
                        }
                        updateView(color);
                    }
                });
    }

    private String colorHex(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "0x%02X%02X%02X%02X", a, r, g, b);
    }
}
