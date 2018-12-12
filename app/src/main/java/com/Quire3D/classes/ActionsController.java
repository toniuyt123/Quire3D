package com.Quire3D.classes;

import com.Quire3D.classes.actions.Action;

import java.util.ArrayList;

public class ActionsController {
    private static final ActionsController Instance = new ActionsController();
    private static ArrayList<Action> actions = new ArrayList<>();
    private static int index = -1;

    public static ActionsController getInstance() {
        return Instance;
    }

    private ActionsController() {
    }

    public void addAction(Action action) {
        if(index == actions.size() - 1) {
            actions.add(action);
            index++;
        } else {
            actions.subList(index + 1, actions.size()).clear();
            addAction(action);
        }
    }

    public void undo() {
        if(index > 0) {
            index--;
            actions.get(index).execute(true);
        }
    }

    public void redo() {
        if(index < actions.size() - 1) {
            index++;
            actions.get(index).execute(false);
        }
    }
}
