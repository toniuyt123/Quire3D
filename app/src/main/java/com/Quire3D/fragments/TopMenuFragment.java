package com.Quire3D.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.Quire3D.classes.ActionsController;
import com.Quire3D.virosample.R;

public class TopMenuFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_top_menu, container, false);

        Button importButton = view.findViewById(R.id.ImportButton);
        importButton.setOnClickListener(this);
        Button undo = view.findViewById(R.id.Undo);
        undo.setOnClickListener(this);
        Button redo = view.findViewById(R.id.Redo);
        redo.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Undo:
                ActionsController.getInstance().undo();
                break;
            case R.id.Redo:
                ActionsController.getInstance().redo();
                break;
        }
    }
}
