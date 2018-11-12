/*
 * Copyright (c) 2017-present, Viro, Inc.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Quire3D.activities;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View;
import com.Quire3D.virosample.R;
import com.viro.core.Node;

import com.viro.core.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import com.Quire3D.classes.OrbitCamera;
import com.Quire3D.classes.Handles;

/**
 * A sample Android activity for creating 3D scenes in a View.
 * <p>
 * Extend and override onRendererStart() to start building your 3D scenes.
 */
public class ViroActivity extends Activity {

    private static final String TAG = ViroActivity.class.getSimpleName();
    protected ViroView mainView;
    private AssetManager mAssetManager;
    private Scene scene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLayout);
        mainView = new ViroViewScene(this, new ViroViewScene.StartupListener() {
            @Override
            public void onSuccess() {
                createWorld();
            }

            @Override
            public void onFailure(ViroViewScene.StartupError error, String errorMessage) {
                Log.e("fail", "Scene load failed");
            }
        });

        layout.addView(mainView, 1080, 1920);

        Button button = (Button) findViewById(R.id.SelectFile);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

    }

    private void createWorld() {
        scene = new Scene();
        Node rootNode = scene.getRootNode();

        Geometry cube = new Box(1f,1f,1f);
        Material boxMaterial = new Material();
        boxMaterial.setDiffuseColor(Color.WHITE);
        boxMaterial.setLightingModel(Material.LightingModel.LAMBERT);
        cube.setMaterials(Arrays.asList(boxMaterial));

        Node cubeNode = new Node();
        cubeNode.setGeometry(cube);
        cubeNode.setClickListener(new ClickListener() {
            @Override
            public void onClick(int i, Node node, Vector vector) {
                makeNodeSelectable(node);
            }

            @Override
            public void onClickState(int i, Node node, ClickState clickState, Vector vector) {

            }
        });


        Spotlight spotlight = new Spotlight();
        spotlight.setPosition(new Vector(-1f, 4f, 3));
        spotlight.setDirection(new Vector(0, 0, -1));
        spotlight.setAttenuationStartDistance(5);
        spotlight.setAttenuationEndDistance(10);
        spotlight.setInnerAngle(5);
        spotlight.setOuterAngle(20);
        spotlight.setColor(Color.GRAY);
        spotlight.setIntensity(800);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(Color.RED);
        ambient.setIntensity(200);

        Node lightNode = new Node();
        lightNode.addLight(spotlight);
        lightNode.addLight(ambient);
        Node cameraNode = new Node();
        OrbitCamera camera = new OrbitCamera(cameraNode, mainView);

        Geometry grid = new Quad(5, 5);
        Material gridTexture = new Material();
        gridTexture.setDiffuseTexture(new Texture(bitmapFromAsset("floor_grid.png"),Texture.Format.RGBA8, true, false));
        gridTexture.setCullMode(Material.CullMode.NONE);
        grid.setMaterials(Arrays.asList(gridTexture));
        Node gridNode = new Node();
        gridNode.setGeometry(grid);
        gridNode.setRotation(new Quaternion(-(float)Math.PI / 2f, 0f, 0f));

        rootNode.addChildNode(cubeNode);
        rootNode.addChildNode(lightNode);
        rootNode.addChildNode(cameraNode);
        rootNode.addChildNode(gridNode);

        mainView.setPointOfView(cameraNode);
        mainView.setScene(scene);
    }

    public void makeNodeSelectable(Node node) {
        Handles handles = new Handles(getView(), "file:///android_asset/translate_handle.obj");
        handles.setParent(node);
    }


    private Bitmap bitmapFromAsset(String assetName) {
        if (mAssetManager == null) {
            mAssetManager = getResources().getAssets();
        }

        InputStream imageStream;
        try {
            imageStream = mAssetManager.open(assetName);
        } catch (IOException exception) {
            Log.w("Viro", "Unable to find image [" + assetName + "] in assets! Error: "
                    + exception.getMessage());
            return null;
        }
        return BitmapFactory.decodeStream(imageStream);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainView.onActivityStarted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainView.onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainView.onActivityPaused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainView.onActivityStopped(this);
    }

    public Scene getScene() {
        return scene;
    }

    public ViroView getView() {
        return mainView;
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
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        StringBuilder text = new StringBuilder();

        try {
            InputStream is = getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            Log.d("improtError", "file not found");
        }
        Log.i("textFile", "text is: " + text.toString());
    }

}