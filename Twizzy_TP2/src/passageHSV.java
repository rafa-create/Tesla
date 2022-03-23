import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class passageHSV {
	public static void main(String[] args) {
		Mat m= main.lectureImage("hsv.png");
		Mat output = Mat.zeros(m.size(),m.type());
		//Imgproc.cvtColor(m, output, Imgproc.COLOR_BGR2HSV);
		main.ImShow("HSV",output);
		Vector<Mat> channels = new Vector<Mat>();
		
	}
}
