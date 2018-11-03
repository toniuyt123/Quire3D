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
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.Quire3D.virosample.R;
import com.viro.core.Node;

import com.viro.core.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.Quire3D.classes.OrbitCamera;

/**
 * A sample Android activity for creating 3D scenes in a View.
 * <p>
 * Extend and override onRendererStart() to start building your 3D scenes.
 */
public class ViroActivity extends Activity {

    private static final String TAG = ViroActivity.class.getSimpleName();
    protected ViroView mainView;
    private AssetManager mAssetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout layout = (FrameLayout) findViewById(R.id.frameLayout);
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
    }

    private void createWorld() {
        // Create a new Scene and get its root Node
        Scene scene = new Scene();
        Node rootNode = scene.getRootNode();

        Geometry cube = new Box(1f,1f,1f);
        Material boxMaterial = new Material();
        boxMaterial.setDiffuseColor(Color.WHITE);
        boxMaterial.setLightingModel(Material.LightingModel.PHONG);
        cube.setMaterials(Arrays.asList(boxMaterial));

        Spotlight spotlight = new Spotlight();
        spotlight.setPosition(new Vector(0, -0.25f, 5));
        spotlight.setDirection(new Vector(0, 0, -1));
        spotlight.setAttenuationStartDistance(5);
        spotlight.setAttenuationEndDistance(10);
        spotlight.setInnerAngle(5);
        spotlight.setOuterAngle(20);
        spotlight.setColor(Color.GRAY);
        spotlight.setIntensity(800);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(Color.WHITE);
        ambient.setIntensity(150);

        Node lightNode = new Node();
        lightNode.addLight(spotlight);
        lightNode.addLight(ambient);

        Node cubeNode = new Node();
        cubeNode.setGeometry(cube);
        //cubeNode.setRotation(new Quaternion((float)Math.PI / 4f, 0, (float)Math.PI / 4f));
        // Display the scene

        Node cameraNode = new Node();
        OrbitCamera camera = new OrbitCamera(cameraNode, mainView);

        cubeNode.setDragListener(new DragListener() {
            @Override
            public void onDrag(int i, Node node, Vector world, Vector local) {
                node.setPosition(local);
            }
        });

        cubeNode.setGesturePinchListener(new GesturePinchListener() {
            @Override
            public void onPinch(int i, Node node, float v, PinchState pinchState) {
                if(pinchState == PinchState.PINCH_MOVE) {
                    Vector currentScale = node.getScaleRealtime();
                    node.setScale(new Vector(currentScale.x * v, currentScale.y * v, currentScale.z * v));
                }
            }
        });

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
}