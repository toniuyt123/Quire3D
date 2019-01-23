package com.Quire3D.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.classes.ActionsController;
import com.Quire3D.classes.actions.ChangeColorAction;
import com.Quire3D.classes.actions.ChangeLightModelAction;
import com.Quire3D.virosample.R;
import com.viro.core.Material;
import com.viro.core.Node;

import java.util.List;
import java.util.Locale;

import top.defaults.colorpicker.ColorPickerPopup;

public class MaterialsFragment extends ObjectParamsFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private View colorView;
    private TextView hexColor;
    private Material.LightingModel[] lightingModels = new Material.LightingModel[]{Material.LightingModel.LAMBERT, Material.LightingModel.BLINN,
                                                                                Material.LightingModel.PHONG, Material.LightingModel.CONSTANT};
    private Spinner lightModelSpinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_materials, container, false);

        try {
            TableRow colorPicker = view.findViewById(R.id.ColorPicker);
            colorPicker.setOnClickListener(this);
            colorView = view.findViewById(R.id.colorView);
            hexColor = view.findViewById(R.id.hexColor);
            lightModelSpinner = view.findViewById(R.id.light_model_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                    R.array.light_model_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lightModelSpinner.setAdapter(adapter);
            lightModelSpinner.setOnItemSelectedListener(this);

            update(ViroActivity.getSelectedNode());
        } catch (NullPointerException e) {
            e.printStackTrace();
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

    @Override
    public void update(Node node) {
        Material mat = node.getGeometry().getMaterials().get(0);
        int color = mat.getDiffuseColor();
        colorView.setBackgroundColor(color);
        hexColor.setText(colorHex(color));

        Material.LightingModel lightModel = mat.getLightingModel();
        for(int i = 0;i < lightingModels.length;i++) {
            if(lightModel.equals(lightingModels[i])) {
                lightModelSpinner.setSelection(i);
            }
        }
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
                            update(selected);
                        } catch (NullPointerException e) {
                            e.getMessage();
                        }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            Node selected = ViroActivity.getSelectedNode();
            Material material = selected.getGeometry().getMaterials().get(0);

            Material.LightingModel newModel = lightingModels[(int)id];
            ActionsController.getInstance().addAction(new ChangeLightModelAction(selected, material.getLightingModel(), newModel));
            material.setLightingModel(newModel);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
