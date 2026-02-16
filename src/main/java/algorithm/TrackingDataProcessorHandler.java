package algorithm;

import inputOutput.TempTool;
import inputOutput.AbstractTrackingDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class TrackingDataProcessorHandler handles access to tracking data
 * from package InputOutput. It manages all collected tracking data.
 */
class TrackingDataProcessorHandler {

    List<TrackingTool> trackingTools = new ArrayList<>();
    private AbstractTrackingDataSource source;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public List<TrackingTool> getToolMeasures() {
        return trackingTools;
    }

    /**
     * Restarts all measurements: resets
     * the internal list of tools.
     */
    public void restartMeasurements() {
        trackingTools = new ArrayList<>();
    }

    /**
     * The method getNextData calls method update, which is from inputOutput,
     * creates from return value of update a measurement and adds this tool
     *
     * @param countToGetNext - number of the reloaded data
     * @param countToGetNext ,
     * @return toolMeasures
     */
    public List<TrackingTool> getNextData(int countToGetNext) {

        if (source == null) {
            logger.log(Level.WARNING,"Tracking data source is not set. Aborting!");
            return trackingTools;
        }

        for (double i = 1; i <= countToGetNext; i++) {
            /* from return value of update a new measurement will be created */
            List<TempTool> tempTools = source.getLastToolList();

            if (tempTools.isEmpty()) {
                logger.log(Level.WARNING,"Toollist is empty.");
                break;
            }

            for (TempTool tempTool : tempTools) {
                TrackingData trackingData = new TrackingData(tempTool);
                addMeasurementToTool(trackingData);
            }

        }

        return trackingTools;
    }

    public AbstractTrackingDataSource getSource() {
        return source;
    }

    /**
     * @param source Sets the TrackingDataSource that is used to get all data.
     */
    public void setSource(AbstractTrackingDataSource source) {
        this.source = source;
    }

    /**
     * This methods manages the tools. AddMeasurementToTool controls if a tool
     * with this name exists. If there is already a tool with this name, then
     * the method adds the new measurements to this tool. If there is no
     * tool with this name,then a new tool is created
     *
     * @param trackingData - variable of type Measurement
     */

    private void addMeasurementToTool(TrackingData trackingData) {

        /* Check if tool exists */
        for (TrackingTool trackingTool : trackingTools) {
            if (trackingTool.getName().equals(trackingData.getToolname())) {

                /* added new measurements to the tool */
                trackingTool.addMeasurement(trackingData);
                return;
            }
        }

        /* creation of a new tool */
        TrackingTool newTrackingTool = new TrackingTool(trackingData.getToolname());
        newTrackingTool.addMeasurement(trackingData);
        trackingTools.add(newTrackingTool);
    }
}
