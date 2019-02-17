package com.Quire3D.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.util.OBJObject;
import com.Quire3D.virosample.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TopMenuFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_top_menu, container, false);

        Button importButton = view.findViewById(R.id.ImportButton);
        importButton.setOnClickListener(this);
        Button exportButton = view.findViewById(R.id.ExportButton);
        exportButton.setOnClickListener(this);
        Button undo = view.findViewById(R.id.Undo);
        undo.setOnClickListener(this);
        Button redo = view.findViewById(R.id.Redo);
        redo.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ImportButton:
                chooseFile();
                break;
            case R.id.ExportButton:
                OBJObject.showFileNameDialog(getActivity());
                break;
            case R.id.Undo:
                ActionsController.getInstance().undo();
                break;
            case R.id.Redo:
                ActionsController.getInstance().redo();
                break;
        }
    }

    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
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
        Uri uri = data.getData();
        StringBuilder text = new StringBuilder();

        if(uri != null) {
            try {
                InputStream is = getActivity().getContentResolver().openInputStream(uri);
                assert is != null;
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                Log.d("importError", "file not found");
            }
        }
        OBJObject imported = new OBJObject(text.toString());
        ViroActivity.getScene().getRootNode().addChildNode(imported);
        ViroActivity activity = (ViroActivity) getActivity();
        activity.makeNodeSelectable(imported);
        HierarchyFragment hierarchy = (HierarchyFragment) getActivity().getFragmentManager().findFragmentById(R.id.hierarchyFragment);
        hierarchy.addToHierarchy(imported, 0);
    }
}
