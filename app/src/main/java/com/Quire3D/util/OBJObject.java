package com.Quire3D.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;

import com.Quire3D.fragments.HierarchyFragment;
import com.Quire3D.fragments.MaterialsFragment;
import com.viro.core.Geometry;
import com.viro.core.Matrix;
import com.viro.core.Node;
import com.viro.core.Object3D;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.viro.core.Submesh;
import com.viro.core.Vector;

public class OBJObject extends Object3D {
    private List<Vector> normals = new ArrayList<>();
    private List<Vector> correctNormals = new ArrayList<>();
    private List<Vector> textureCoords = new ArrayList<>();
    private List<Vector> vertices = new ArrayList<>();
    private List<Integer> triangleIndeces = new ArrayList<>();
    private List<Integer> normalIndeces = new ArrayList<>();
    //private List<Submesh> submeshes;

    public OBJObject(String textFile) {
        Scanner scanner = new Scanner(textFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String element = line.substring(0,2);

            switch (element) {
                case "v ":
                    addEntryTo(vertices, line);
                    break;
                case "vt":
                    addEntryTo(textureCoords, line);
                    break;
                case "vn":
                    addEntryTo(normals, line);
                    break;
                case "f ":
                    String[] faces = line.split("\\s+");
                    for (int i = 1; i <= faces.length - 1; i++) {
                        String[] indeces = faces[i].split("/");
                        triangleIndeces.add(
                                Integer.parseInt(indeces[0]) - 1);

                        normalIndeces.add(
                                Integer.parseInt(indeces[indeces.length - 1]) - 1);
                    }

                    break;
                case "o ":
                    setName(line.substring(2));
                    break;
            }
        }
        scanner.close();

        //fixNormals();
        Submesh.SubmeshBuilder builder = new Submesh.SubmeshBuilder();
        builder.triangleIndices(triangleIndeces);
        Submesh mesh = builder.build();

        Geometry.GeometryBuilder gbuilder = new Geometry.GeometryBuilder();
        gbuilder.submeshes(Arrays.asList(mesh));
        gbuilder.materials(Arrays.asList(
                MaterialsFragment.getMaterials().get(0)
        ));

        Geometry geometry = gbuilder.build();
        geometry.setVertices(vertices);
        geometry.setNormals(correctNormals);

        setGeometry(geometry);

    }

    private void fixNormals() {
        for(int i = 0;i < triangleIndeces.size();i++) {
            correctNormals.add(triangleIndeces.get(i), normals.get(normalIndeces.get(i)));
        }
    }

    private void addEntryTo(List<Vector> list, String line) {
        String[] entry = line.split("\\s+");
        list.add(new Vector(
                Float.parseFloat(entry[1]),
                Float.parseFloat(entry[2]),
                Float.parseFloat(entry[3])
        ));
    }


    public OBJObject(List<Vector> normals, List<Vector> textureCoords, List<Vector> vertices) {
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.vertices = vertices;
    }

    public OBJObject(List<Vector> normals, List<Vector> vertices) {
        this.normals = normals;
        this.vertices = vertices;
    }

    public List<Vector> getNormals() {
        return normals;
    }

    public List<Vector> getTextureCoords() {
        return textureCoords;
    }

    public List<Vector> getVertices() {
        return vertices;
    }

    public List<Integer> getTriangleIndeces() {
        return triangleIndeces;
    }

    public static void exportOBJ(Context c, String fileName) {
        StringBuilder output = new StringBuilder();
        String ln = System.getProperty("line.separator");
        output.append("# Quire3D v0.1 OBJ file").append(ln);

        ArrayList<Node> nodes = HierarchyFragment.getExportableObjects();
        for(int i = 0;i < nodes.size();i++) {
            Node n = nodes.get(i);
            output.append("o ").append(n.getName()).append(ln);

            Matrix transformMatrix = n.getWorldTransformRealTime();
            if(n.getClass().equals(OBJObject.class)){
                Log.i("exported", "we in");
                OBJObject obj = (OBJObject) n;
                for(Vector v: obj.getVertices()){
                    output.append("v ").append(transformMatrix.multiply(v).toString()).append(ln);
                }
                for(Vector vn: obj.getNormals()){
                    output.append("vn ").append(transformMatrix.multiply(vn).normalize().toString()).append(ln);
                }
                for(Vector vt: obj.getTextureCoords()){
                    output.append("vt ").append(vt.toString()).append(ln);
                }

            }
        }

        exportFile(c, fileName, output.toString());
    }

    public static void showFileNameDialog(final Context c){
        final EditText editText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("File name")
                .setMessage("Enter name")
                .setView(editText)
                .setPositiveButton("Export", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exportOBJ(c, String.valueOf(editText.getText()));
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private static void exportFile(Context context, String filename, String body){
        Log.i("exported", context.getFilesDir().toString());
        Log.i("exported", body);
        File file = new File(context.getFilesDir(), filename + ".obj");

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
