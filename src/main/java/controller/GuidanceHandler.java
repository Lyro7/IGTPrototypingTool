package controller;

import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;

public class GuidanceHandler {

    private MainController mainController;

    private final List<Controller> guidanceControllers = new ArrayList<>();

    private AnimationTimer animator;

    public GuidanceHandler() {
        navigationLoop();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void switchToTab(String fileName) {
        mainController.switchContentOfTab(fileName);
    }

    public void navigationLoop() {
        animator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //TODO
            }
        };
    }

    public void startNavigationLoop() {
        animator.start();
    }

    public void stopNavigationLoop() {
        animator.stop();
    }

    public void resetControllers() {
        for (Controller controller : guidanceControllers) {
            controller.close();
        }
        guidanceControllers.clear();
    }

    public void addGuidanceController(Controller controller) {
        this.guidanceControllers.add(controller);
    }

}
