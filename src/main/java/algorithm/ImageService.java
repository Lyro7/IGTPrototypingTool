package algorithm;

import java.awt.image.BufferedImage;

import inputOutput.VideoSource;
import javafx.scene.image.Image;
import org.opencv.core.Mat;

/**
 * This class is the central interface to image data sources. The current image can be accessed using the readImg(),
 * readBufImg(), or readMat() methods. Internally, the ImageDataProcessor class is used, in which the logic for
 * accessing the image data sources is implemented.
 */
public class ImageService {

    ImageDataProcessor dataProcessor = new ImageDataProcessor();

    public boolean openConnection(VideoSource source){
        return dataProcessor.openConnection(source);
    }
    public boolean openConnection(VideoSource source, int deviceId){
        return dataProcessor.openConnection(source, deviceId);
    }

    public boolean closeConnection(){
        return dataProcessor.closeConnection();
    }

    public BufferedImage readBufImg() {
        return dataProcessor.readBufImg();
    }

    public Image readImg() {
        return dataProcessor.readImg();
    }

    public Mat readMat(){return dataProcessor.readMat();}

    public ImageDataProcessor getDataProcessor() {
        return this.dataProcessor;
    }
}
