package com.Quire3D.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.viro.core.ViroMediaRecorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

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
        if(resultCode == RESULT_OK) {
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

                Uri correct = Uri.fromFile(new File(uri.getLastPathSegment().substring(4)));
                /*Log.d("filepath", uri.toString());
                Uri fileUri = Uri.fromFile(new File(uri.getPath()));
                Log.d("filepath", fileUri.getLastPathSegment());
                Log.d("filepath", fileUri.toString());
                Log.d("filepath", correct.getPath());
                Log.d("filepath", correct.toString());*/
                CreatePrimitiveFragment frag = (CreatePrimitiveFragment) getFragmentManager().findFragmentById(R.id.createPrimitiveFragment);
                OBJObject model = new OBJObject(ViroActivity.getView().getViroContext(), correct, text.toString(), frag);
            }
        }
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
        toogleUnwantedNodesForScreenshot(false);
        recorder.takeScreenShotAsync("viro_screenshot", true, new ViroMediaRecorder.ScreenshotFinishListener() {

            @Override
            public void onSuccess(Bitmap bitmap, String s) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity().getBaseContext(), "Image saved", Toast.LENGTH_LONG).show();
                    }
                });
                toogleUnwantedNodesForScreenshot(true);
            }

            @Override
            public void onError(ViroMediaRecorder.Error error) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity().getBaseContext(), "Failed to save image", Toast.LENGTH_LONG).show();
                    }
                });
                toogleUnwantedNodesForScreenshot(true);
            }
        });
    }

    private void toogleUnwantedNodesForScreenshot(boolean areActive) {
        ViroActivity.getGridNode().setVisible(areActive);
        if(ViroActivity.getActiveHandles() != null){
            ViroActivity.getActiveHandles().getHandleRoot().setVisible(areActive);
        }
    }

    public static void exportFile(Context context, String filename, String body) {
        String state = Environment.getExternalStorageState();
        //external storage availability check
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), filename);

        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file, true);

            outputStream.write(body.getBytes());
            outputStream.flush();
            outputStream.close();
            Toast.makeText(context, "Created", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
