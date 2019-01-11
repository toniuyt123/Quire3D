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
import android.app.Fragment;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.Quire3D.fragments.HierarchyFragment;
import com.Quire3D.virosample.R;
import com.viro.core.Node;

import com.viro.core.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import com.Quire3D.classes.OrbitCamera;
import com.Quire3D.classes.Handles;
import com.Quire3D.classes.TranslateHandles;

public class ViroActivity extends Activity {
    protected static ViroView mainView;
    private AssetManager mAssetManager;
    private static Scene scene;
    private static Node selectedNode;
    private Fragment paramsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainView = findViewById(R.id.SceneView);
        createWorld(mainView);

        int[] resolution = getScreenResolution();
        Log.i("resolution", "width: " + Integer.toString(resolution[0]) + "height: " + Integer.toString(resolution[1]));
        mainView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

        this.paramsFrag = getFragmentManager().findFragmentById(R.id.objectParamsFragment);
    }

    private void createWorld(ViroView view) {
        scene = new Scene();
        Node rootNode = scene.getRootNode();

        Geometry cube = new Box(1f,1f,1f);
        Material boxMaterial = new Material();
        boxMaterial.setDiffuseColor(Color.GRAY);
        boxMaterial.setLightingModel(Material.LightingModel.LAMBERT);
        cube.setMaterials(Arrays.asList(boxMaterial));

        Node cubeNode = new Node();
        cubeNode.setGeometry(cube);
        makeNodeSelectable(cubeNode);

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
        ambient.setColor(Color.GRAY);
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

        cubeNode.setName("Cube");
        lightNode.setName("Light");
        cameraNode.setName("Camera");
        gridNode.setName("floor_grid");
        rootNode.addChildNode(cubeNode);
        rootNode.addChildNode(lightNode);
        rootNode.addChildNode(cameraNode);
        rootNode.addChildNode(gridNode);

        view.setPointOfView(cameraNode);
        view.setScene(scene);

        HierarchyFragment.updateHierarchy();
    }

    public static void makeNodeSelectable(Node node) {
        node.setClickListener(new ClickListener() {
            @Override
            public void onClick(int i, Node node, Vector vector) {
                if(selectedNode != node) {
                    if(selectedNode != null) {
                        for(Node n: selectedNode.getChildNodes()) {
                            if(n.getName().equals("Handles")) {
                                n.disposeAll(true);
                                break;
                            }
                        }
                    }
                    Handles handles = new TranslateHandles(getView(), node);
                    selectedNode = node;
                }
            }

            @Override
            public void onClickState(int i, Node node, ClickState clickState, Vector vector) {

            }
        });
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

    public static Scene getScene() {
        return scene;
    }

    public static ViroView getView() {
        return mainView;
    }

    private int[] getScreenResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int[] resolution = {displayMetrics.widthPixels, displayMetrics.heightPixels};

        Resources resources = getView().getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            resolution[0] += resources.getDimensionPixelSize(resourceId);
        }
        return resolution;
    }

    public static Node getSelectedNode() {
        return selectedNode;
    }
}