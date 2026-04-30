package util;

import controller.GuidanceHandler;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;

// Reacts to key events pressed in the guidance tab
public class GuidanceKeyHandler {

    private Tab guidanceTab;

    private final GuidanceHandler guidanceHandler;

    public GuidanceKeyHandler(GuidanceHandler guidanceHandler) {
        this.guidanceHandler = guidanceHandler;
    }

    public void setContentNode(Tab guidanceTab) {
        this.guidanceTab = guidanceTab;
    }

    public void handleKeyPressed(Scene scene) {
        if (guidanceTab != null) {
            scene.setOnKeyPressed(keyEvent -> {
                if (guidanceTab.isSelected()) {
                    // Switch to next phase
                    if (keyEvent.getCode() == KeyCode.Q) {
                        if (guidanceHandler.getCurrentPhase() == GuidanceHandler.Phase.ALIGNMENT) {
                            guidanceHandler.updateCurrentPhase(GuidanceHandler.Phase.ANGLE);
                        } else if (guidanceHandler.getCurrentPhase() == GuidanceHandler.Phase.ALIGNMENT) {
                            guidanceHandler.updateCurrentPhase(GuidanceHandler.Phase.DEPTH);
                        }
                    }
                    // Switch to last phase
                    if (keyEvent.getCode() == KeyCode.E) {
                        if (guidanceHandler.getCurrentPhase() == GuidanceHandler.Phase.ANGLE) {
                            guidanceHandler.updateCurrentPhase(GuidanceHandler.Phase.ALIGNMENT);
                        } else if (guidanceHandler.getCurrentPhase() == GuidanceHandler.Phase.DEPTH) {
                            guidanceHandler.updateCurrentPhase(GuidanceHandler.Phase.ANGLE);
                        }
                    }
                }
            });
        }
    }
}
