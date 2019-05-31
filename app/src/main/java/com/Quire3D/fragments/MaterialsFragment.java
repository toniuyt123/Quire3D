package com.Quire3D.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.util.actions.ChangeColorAction;
import com.Quire3D.util.actions.ChangeLightModelAction;
import com.Quire3D.util.actions.ChangeTextureAction;
import com.Quire3D.virosample.R;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import top.defaults.colorpicker.ColorPickerPopup;

import static android.app.Activity.RESULT_OK;

public class MaterialsFragment extends ObjectParamsFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private View colorView;
    private TextView hexColor;
    private Material.LightingModel[] lightingModels =
            new Material.LightingModel[]{Material.LightingModel.LAMBERT, Material.LightingModel.BLINN,
                                    Material.LightingModel.PHONG, Material.LightingModel.CONSTANT};
    private Spinner lightModelSpinner, materialsSpinner;
    private static ArrayList<Material> savedMaterials = new ArrayList<>(Arrays.asList(makeDefaultMat()));
    private Material selectedMat;
    private ArrayAdapter<String> materialAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_materials, container, false);

        try {
            colorView = view.findViewById(R.id.colorView);
            colorView.setOnClickListener(this);
            hexColor = view.findViewById(R.id.hexColor);
            hexColor.setOnClickListener(this);
            Button addMaterial = view.findViewById(R.id.addMaterial);
            addMaterial.setOnClickListener(this);
            Button assignMaterial = view.findViewById(R.id.AssignMaterial);
            assignMaterial.setOnClickListener(this);
            Button importTexture = view.findViewById(R.id.importTexture);
            importTexture.setOnClickListener(this);

            lightModelSpinner = view.findViewById(R.id.light_model_spinner);
            ArrayAdapter<CharSequence> lightModelAdapter = ArrayAdapter.createFromResource(view.getContext(),
                    R.array.light_model_array, android.R.layout.simple_spinner_item);
            lightModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lightModelSpinner.setAdapter(lightModelAdapter);
            lightModelSpinner.setOnItemSelectedListener(this);

            materialsSpinner = view.findViewById(R.id.materials_spinner);
            materialAdapter = new ArrayAdapter<>(view.getContext(),
                    android.R.layout.simple_spinner_item, new ArrayList<String>());

            for(int i = 0;i < savedMaterials.size();i++) {
                materialAdapter.add(savedMaterials.get(i).getName());
            }

            materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            materialsSpinner.setAdapter(materialAdapter);
            materialsSpinner.setOnItemSelectedListener(this);

            update(getMaterials().get(0));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.colorView:
                popup();
                break;
            case R.id.addMaterial:
                showAddMatDialog(ViroActivity.getView().getContext());
                break;
            case R.id.AssignMaterial:
                try {
                    Node selected = ViroActivity.getSelectedNode();
                    selected.getGeometry().setMaterials(Arrays.asList(selectedMat));
                } catch (NullPointerException e) {
                    e.getMessage();
                }
                break;
            case R.id.importTexture:
                chooseFile();
                break;
        }
    }

    @Override
    public void update(Node node) {
        Material mat = node.getGeometry().getMaterials().get(0);
        update(mat);
    }

    public void update(Material mat) {
        selectedMat = mat;
        int color = mat.getDiffuseColor();
        colorView.setBackgroundColor(color);
        hexColor.setText(colorHex(color));

        Material.LightingModel lightModel = mat.getLightingModel();
        for(int i = 0;i < lightingModels.length;i++) {
            if(lightModel.equals(lightingModels[i])) {
                lightModelSpinner.setSelection(i);
            }
        }

        String name = mat.getName();
        for(int i = 0;i < materialAdapter.getCount();i++) {
            if(name.equals(materialAdapter.getItem(i))) {
                materialsSpinner.setSelection(i);
            }
        }
    }

    private void showAddMatDialog(Context c) {
        final EditText editText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Material name")
                .setMessage("Enter name")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(editText.getText());
                        addNewMaterial(name);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    public void addNewMaterial(String name){
        Material newMat = makeDefaultMat();
        newMat.setName(name);
        materialAdapter.add(name);
        savedMaterials.add(newMat);
        update(newMat);
    }

    private void popup() {
        new ColorPickerPopup.Builder(ViroActivity.getView().getContext())
                .initialColor(selectedMat.getDiffuseColor())
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
                        ActionsController.getInstance().addAction(
                                new ChangeColorAction(selectedMat.getDiffuseColor(), color, selectedMat));

                        selectedMat.setDiffuseColor(color);
                        update(selectedMat);
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
        switch(parent.getId()){
            case R.id.light_model_spinner:
                try {
                    Material.LightingModel newModel = lightingModels[(int)id];
                    ActionsController.getInstance().addAction(new ChangeLightModelAction(selectedMat.getLightingModel(), newModel, selectedMat));
                    selectedMat.setLightingModel(newModel);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.materials_spinner:
                Material selected = savedMaterials.get((int) id);
                update(selected);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static Material makeDefaultMat() {
        Material defaultMat = new Material();
        defaultMat.setDiffuseColor(Color.WHITE);
        defaultMat.setLightingModel(Material.LightingModel.LAMBERT);
        defaultMat.setCullMode(Material.CullMode.NONE);
        defaultMat.setName("Default");

        return defaultMat;
    }

    public static List<Material> getMaterials(){
        return savedMaterials;
    }

    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    1);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ViroActivity.getView().getContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Uri uri = data.getData();

            if(uri != null) {
                try {
                    InputStream is = getActivity().getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Texture newTexture = new Texture(bitmap, Texture.Format.RGBA8, true, false);

                    ActionsController.getInstance().addAction(
                            new ChangeTextureAction(selectedMat.getDiffuseTexture(), newTexture, selectedMat));
                    selectedMat.setDiffuseTexture(newTexture);
                }
                catch (IOException e) {
                    Log.d("importError", "file not found");
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void exportmtl(Context context, String filename) {
        StringBuilder output = new StringBuilder();
        String ln = System.getProperty("line.separator");
        output.append("# Quire3D mtl file").append(ln);
        output.append("# Material count: ").append(savedMaterials.size()).append(ln);
        output.append(ln);

        for(Material material: savedMaterials){
            output.append("newmtl ").append(material.getName()).append(ln);
            Color c = Color.valueOf(material.getDiffuseColor());
            output.append("Ns ").append(material.getShininess()).append(ln);;
            output.append("Kd ").append(c.red()).append(" ")
                                .append(c.green()).append(" ")
                                .append(c.blue()).append(ln);

            output.append(ln);
        }

        TopMenuFragment.exportFile(context, filename + ".mtl",output.toString());
    }
}






































