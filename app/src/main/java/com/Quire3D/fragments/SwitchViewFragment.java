package com.Quire3D.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.Quire3D.activities.ViroActivity;
import com.Quire3D.virosample.R;


public class SwitchViewFragment extends Fragment implements View.OnClickListener {
    private static int containerId;
    private MaterialsFragment materialsFragment;
    private PositionalDataFragment posFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_switch_view, container, false);

        ImageButton objectParams = view.findViewById(R.id.ObjectParams);
        objectParams.setOnClickListener(this);
        ImageButton materials = view.findViewById(R.id.Materials);
        materials.setOnClickListener(this);
        ImageButton other = view.findViewById(R.id.Other);
        other.setOnClickListener(this);

        materialsFragment = new MaterialsFragment();
        posFragment = new PositionalDataFragment();

        containerId = R.id.objectViewContainer;

        replaceFragment(containerId, posFragment);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ObjectParams:
                replaceFragment(containerId, posFragment);
                break;
            case R.id.Materials:
                replaceFragment(containerId, materialsFragment);
                break;
            case R.id.Other:

                break;
        }
    }

    private void replaceFragment(int oldFragId, Fragment newFrag){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(oldFragId, newFrag);
        transaction.commit();
    }

    public static int getCurrentId() {
        return containerId;
    }
}
