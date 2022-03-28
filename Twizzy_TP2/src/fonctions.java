import java.awt.Image;
import java.awt.Point;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;


public class fonctions{
	public static List<MatOfPoint> DetecterContours(Mat threshold_img){
		//Mat threshold_img = seuillage2.DetecterCercles(m);
		//main.ImShow("Seuillage", threshold_img);
		int thresh = 100;
		Mat canny_output = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchy = new MatOfInt4();
		Imgproc.Canny(threshold_img, canny_output, thresh, thresh*2);
		Imgproc.findContours(canny_output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Mat drawing = Mat.zeros(canny_output.size(), CvType.CV_8UC3);
		Random rand = new Random();
		for(int i=0;i<contours.size();i++) {
			Scalar color = new Scalar(rand.nextInt(255 - 0 + 1),rand.nextInt(255 - 0 + 1),rand.nextInt(255 - 0 + 1));
			Imgproc.drawContours(drawing, contours, i, color,1);
		}
		main.ImShow("Contours", drawing);
		return contours;
	}
	
	// PASSAGE HSV PAS FAIT 
	/*public static void passageHSV() {
		Mat m= main.lectureImage("hsv.png");
		Mat output = Mat.zeros(m.size(),m.type());
		//Imgproc.cvtColor(m, output, Imgproc.COLOR_BGR2HSV);
		main.ImShow("HSV",output);
		Vector<Mat> channels = new Vector<Mat>();
	}*/
	
	public static void seuillage() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m = main.lectureImage("circles.jpg");
		Mat hsv_image = Mat.zeros(m.size(),m.type());
		Imgproc.cvtColor(m, hsv_image,Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = new Mat();
		Core.inRange(hsv_image, new Scalar(0,100,100), new Scalar(10,255,255), threshold_img);
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9,9), 2,2);
		main.ImShow("Cercles rouge", threshold_img);
	}
	
	public static Mat DetecterCercles(Mat m) {
		Mat hsv_image = Mat.zeros(m.size(),m.type());
		Imgproc.cvtColor(m, hsv_image,Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = new Mat();
		Mat threshold_img1 = new Mat();
		Mat threshold_img2 = new Mat();
		Core.inRange(hsv_image, new Scalar(0,100,100), new Scalar(10,255,255), threshold_img1);
		Core.inRange(hsv_image, new Scalar(160,100,100), new Scalar(179,255,255), threshold_img2);
		Core.bitwise_or(threshold_img1, threshold_img2, threshold_img);
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9,9), 2,2);
		
		return threshold_img;
	}
	
	public static void reconnaissance_cercles_rouges() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat m = main.lectureImage("photo9.jpg");
		main.ImShow("Cercles", m);
		/*Mat hsv_image = Mat.zeros(m.size(),m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		main.ImShow("HSV", hsv_image);*/
		Mat threshold_img = seuillage2.DetecterCercles(m);
		main.ImShow("Seuillage", threshold_img);
		List<MatOfPoint> listContours = DetecterContours(threshold_img);
		
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		float[] radius = new float[1];
		Point center = new Point();
		for(int c=0;c<listContours.size();c++) {
			MatOfPoint contour = listContours.get(c);
			double contourArea = Imgproc.contourArea(contour);
			matOfPoint2f.fromList(contour.toList());
			Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
			if((contourArea/(Math.PI*radius[0]*radius[0]))>=0.8) {
				Core.circle(m, center, (int)radius[0], new Scalar(0,255,0),2);
			}
		}
		main.ImShow("Détection des cercles rouges", m);
	
		// Reconnaissance bales_rouges
		for(int c=0; c<listContours.size();c++) {
			MatOfPoint contour = listContours.get(c);
			double contourArea = Imgproc.contourArea(contour);
			matOfPoint2f.fromList(contour.toList());
			Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
			if((contourArea/(Math.PI*radius[0]*radius[0]))>=0.8){
				Core.circle(m, center, (int)radius[0], new Scalar(0,255,0),2);
				Rect rect = Imgproc.boundingRect(contour);
				Core.rectangle(m,new Point(rect.x,rect.y),
						new Point(rect.x+rect.width,rect.y+rect.height),
						new Scalar(0,255,0),2);
				Mat tmp = m.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
				Mat ball = Mat.zeros(tmp.size(), tmp.type());
				tmp.copyTo(ball);
				main.ImShow("Ball", ball);
				
				// Mise à l'échelle
				Mat sroadSign = Highgui.imread("panneau110.png");
				Mat sObject = new Mat();
				Imgproc.resize(ball, sObject, sroadSign.size());
				Mat grayObject = new Mat(sObject.rows(),sObject.cols(),sObject.type());
				Imgproc.cvtColor(sObject, grayObject, Imgproc.COLOR_BGRA2GRAY);
				Core.normalize(grayObject, grayObject,0,255,Core.NORM_MINMAX);
				Mat graySign = new Mat(sroadSign.rows(),sroadSign.cols(),sroadSign.type());
				Imgproc.cvtColor(sroadSign, graySign, Imgproc.COLOR_BGRA2GRAY);
				Core.normalize(graySign, graySign,0,255,Core.NORM_MINMAX);
				
				//Extraction des descripteurs et keypoints
				FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
				DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
				
				MatOfKeyPoint objectKeypoints = new MatOfKeyPoint();
				orbDetector.detect(grayObject, objectKeypoints);
				
				MatOfKeyPoint signKeypoints = new MatOfKeyPoint();
				orbDetector.detect(grayObject, signKeypoints);
				
				Mat objectDescriptor = new Mat(ball.rows(),ball.cols(),ball.type());
				orbExtractor.compute(grayObject, objectKeypoints, objectDescriptor);
				
				Mat signDescriptor = new Mat(sroadSign.rows(),sroadSign.cols(),sroadSign.type());
				orbExtractor.compute(graySign, signKeypoints, signDescriptor);
				
				// Faire le matching
				MatOfDMatch matchs = new MatOfDMatch();
				DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
				matcher.match(objectDescriptor, signDescriptor,matchs);
				System.out.println(matchs.dump());
				Mat matchedImage = new Mat(sroadSign.rows(),sroadSign.cols()*2,sroadSign.type());
				Features2d.drawMatches(sObject, objectKeypoints, sroadSign, signKeypoints, matchs, matchedImage);
				main.ImShow("matchs", matchedImage);
			}
		}	
	}

}
