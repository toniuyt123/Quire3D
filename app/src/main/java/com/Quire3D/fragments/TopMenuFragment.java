package com.Quire3D.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.util.actions.ActionsController;
import com.Quire3D.util.OBJObject;
import com.Quire3D.virosample.R;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Object3D;
import com.viro.core.ViroMediaRecorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

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
                showExportDialog();
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
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
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
        final Uri uri = data.getData();
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

        Object3D model = new Object3D();
        File modelFile = new File("/storage/emulated/0/Download/untitled.obj");
        Uri fileUri = Uri.fromFile(modelFile);
        Log.i("modelLoading", text.toString());
        Log.i("modelLoading", fileUri.toString());
        Log.i("modelLoading", uri.toString());
        Log.i("modelLoading", uri.isAbsolute() ? "true" : "false");
        model.loadModel(ViroActivity.getView().getViroContext(), fileUri, Object3D.Type.OBJ, new AsyncObject3DListener() {
            public void onObject3DFailed(String error) {
                Log.w("modelLoading", "Failed to load the model" + error);
            }
            public void onObject3DLoaded(Object3D object, Object3D.Type type){

            }
        });
        //OBJObject imported = new OBJObject(ViroActivity.getView().getViroContext(), uri, text.toString());
       /*ViroActivity.getScene().getRootNode().addChildNode(imported);
        ViroActivity activity = (ViroActivity) getActivity();
        activity.makeNodeSelectable(imported);
        HierarchyFragment hierarchy = (HierarchyFragment) getActivity().getFragmentManager().findFragmentById(R.id.hierarchyFragment);
        hierarchy.addToHierarchy(imported, 0);*/
    }

    private void showExportDialog() {
        final Context context = ViroActivity.getView().getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        String[] formats = new String[]{"Image", ".OBJ" };

        final List<String> formatList = Arrays.asList(formats);
        builder.setSingleChoiceItems(formats, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lv = ((AlertDialog)dialog).getListView();
                lv.setTag(new Integer(which));
            }
        });

        builder.setCancelable(true);
        builder.setTitle("Choose format");
        builder.setPositiveButton("Export", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lv = ((AlertDialog)dialog).getListView();
                Integer id = (Integer)lv.getTag();
                switch (id){
                    case 0:
                        exportImage(context);
                        break;
                    case 1:
                        OBJObject.showFileNameDialog(context);
                        break;
                }
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exportImage(final Context context){
        ViroMediaRecorder recorder = ViroActivity.getView().getRecorder();
        ViroActivity.getGridNode().setVisible(false);
        recorder.takeScreenShotAsync("viro_screenshot", true, new ViroMediaRecorder.ScreenshotFinishListener() {

            @Override
            public void onSuccess(Bitmap bitmap, String s) {
                Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();
                ViroActivity.getGridNode().setVisible(true);
            }

            @Override
            public void onError(ViroMediaRecorder.Error error) {
                Toast.makeText(context, "Failed to save image:" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("pedal", error.toString());
                ViroActivity.getGridNode().setVisible(true);
            }
        });
    }

    public static void exportFile(Context context, String filename, String body) {
        Log.i("exported", context.getFilesDir().toString());
        Log.i("exported", body);
        File file = new File(context.getFilesDir(), filename);

        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            writer.append(body);
            writer.close();

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
