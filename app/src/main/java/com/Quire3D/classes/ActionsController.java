package com.Quire3D.classes;

import java.util.ArrayList;

public class ActionsController {
    private static final ActionsController Instance = new ActionsController();
    private static ArrayList<Action> actions = new ArrayList<>();
    private static int index = 0;

    public static ActionsController getInstance() {
        return Instance;
    }

    private ActionsController() {
    }

    public void addAction(Action action) {
        if(index == actions.size()) {
            actions.add(action);
            index++;
        } else {
            actions = (ArrayList) actions.subList(0, index);
            addAction(action);
        }
    }

    public void undo() {
        if(index > 0) {
            actions.get(index - 1).execute();
            index--;
        }
    }

    public void redo() {
        if(index < actions.size()) {
            actions.get(index + 1).execute();
            index++;
        }
    }
}
