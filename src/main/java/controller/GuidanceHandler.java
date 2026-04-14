package controller;

public class GuidanceHandler {

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void switchToTab(String fileName) {
        mainController.switchTabs(fileName);
    }

}
