package com.Quire3D.classes;

import android.util.Log;

import com.viro.core.Object3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.viro.core.Submesh;
import com.viro.core.Vector;

public class OBJObject extends Object3D {
    private Object3D model = new Object3D();
    private List<Vector> normals = new ArrayList<>();
    private List<Vector> textureCoords = new ArrayList<>();
    private List<Vector> vertices = new ArrayList<>();
    private List<Submesh> submeshes;

    public OBJObject(String textFile) {
        Scanner scanner = new Scanner(textFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.substring(0,1).equals("v ")) {
                addEntryTo(line, vertices);
            } else if(line.substring(0,1).equals("vt")) {
                addEntryTo(line, textureCoords);
            } else if(line.substring(0,1).equals("vn")) {
                addEntryTo(line, normals);
            } else if(line.substring(0,1).equals("f ")) {

                String[] faces = line.split("\\s+");
                for(int i = 1; i <= 3; i++) {

                }

            } else if(line.substring(0,1).equals("o ")) {
                setName(line.substring(2));
            }
        }
        scanner.close();

        getGeometry().setNormals(normals);
        getGeometry().setTextureCoordinates(textureCoords);
        getGeometry().setVertices(vertices);
    }

    private void addEntryTo(String line, List list) {
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

    public Object3D getModel() {
        return model;
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
}
