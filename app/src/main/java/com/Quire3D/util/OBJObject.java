package com.Quire3D.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.fragments.CreatePrimitiveFragment;
import com.Quire3D.fragments.HierarchyFragment;
import com.Quire3D.fragments.MaterialsFragment;
import com.Quire3D.fragments.TopMenuFragment;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Material;
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

import com.viro.core.Vector;
import com.viro.core.ViroContext;

public class OBJObject extends Object3D {
    private List<Vector> normals = new ArrayList<>();
    //private List<Vector> correctNormals = new ArrayList<>();
    private List<Vector> textureCoords = new ArrayList<>();
    private List<Vector> vertices = new ArrayList<>();
    private List<Integer> triangleIndeces = new ArrayList<>();
    //private List<Integer> normalIndeces = new ArrayList<>();
    private StringBuilder faces = new StringBuilder();
    //private List<Submesh> submeshes;
    private static String ln = System.getProperty("line.separator");

    public OBJObject(final ViroContext context, final Uri uri, String textFile, final CreatePrimitiveFragment temp) {
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
                    /*String[] faces = line.split("\\s+");
                    for (int i = 1; i <= faces.length - 1; i++) {
                        String[] indeces = faces[i].split("/");
                        triangleIndeces.add(
                                Integer.parseInt(indeces[0]) - 1);

                        normalIndeces.add(
                                Integer.parseInt(indeces[indeces.length - 1]) - 1);
                    }*/
                    faces.append(line).append(ln);
                    break;
                case "o ":
                    setName(line.substring(2));
                    break;
            }
        }
        scanner.close();

        loadModel(context, uri, Type.OBJ, new AsyncObject3DListener(){

            @Override
            public void onObject3DLoaded(Object3D object3D, Type type) {
                Log.i("yeet", "yeet");
                temp.addToScene(OBJObject.this, getName(), true);
            }

            @Override
            public void onObject3DFailed(String s) {
                Log.i("yeet", s);
            }
        });

        /*//fixNormals();
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

        setGeometry(geometry);*/
    }

    /*private void fixNormals() {
        for(int i = 0;i < triangleIndeces.size();i++) {
            correctNormals.add(triangleIndeces.get(i), normals.get(normalIndeces.get(i)));
        }
    }*/

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

    public static void showFileNameDialog(final Context c){
        final EditText editText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("File name")
                .setMessage("Enter name")
                .setView(editText)
                .setPositiveButton("Export", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buildText(c, String.valueOf(editText.getText()));
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    public static void buildText(Context c, String fileName) {
        StringBuilder output = new StringBuilder();
        output.append("# Quire3D v0.1 OBJ file").append(ln);
        output.append("mtllib ").append(fileName).append(".mtl").append(ln);

        ArrayList<Node> nodes = HierarchyFragment.getExportableObjects();
        for(int i = 0;i < nodes.size();i++) {
            Node n = nodes.get(i);

            Matrix transformMatrix = n.getWorldTransformRealTime();
            if(n.getClass().equals(OBJObject.class)){
                OBJObject obj = (OBJObject) n;
                output.append("o ").append(obj.getName()).append(ln);
                for(Vector v: obj.getVertices()){
                    String vectorText = transformMatrix.multiply(v).toString().replace(",", "");
                    output.append("v ").append(vectorText.substring(1, vectorText.length()-1)).append(ln);
                }
                for(Vector vn: obj.getNormals()){
                    String vectorText = transformMatrix.multiply(vn).toString().replace(",", "");
                    output.append("vn ").append(vectorText.substring(1, vectorText.length()-1)).append(ln);
                }
                for(Vector vt: obj.getTextureCoords()){
                    output.append("vt ").append(vt.toString()).append(ln);
                }
                output.append("usemtl ").append(obj.getMaterials().get(0).getName()).append(ln);
                output.append("s off").append(ln);
                output.append(obj.getFacesText());
                output.append(ln);
            }
        }

        TopMenuFragment.exportFile(c, fileName + ".obj", output.toString());
        MaterialsFragment.exportmtl(c, fileName);
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

    public String getFacesText(){
        return faces.toString();
    }
}
