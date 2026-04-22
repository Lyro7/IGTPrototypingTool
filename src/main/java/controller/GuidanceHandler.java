package controller;

import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;

public class GuidanceHandler {

    private MainController mainController;

    /* List, which contains the current active controller */
    private final List<GuidanceController> guidanceControllers = new ArrayList<>();

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
        for (GuidanceController controller : guidanceControllers) {
            controller.close();
        }
        guidanceControllers.clear();
    }

    public void addGuidanceController(GuidanceController controller) {
        this.guidanceControllers.add(controller);
    }


}
