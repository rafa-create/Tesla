package Tweazy;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Imgproc.*;



public class fonctions {


    //passe de BGR � HSV
    public static Mat BGRversHSV(Mat matriceBGR){
        Mat matriceHSV=new Mat(matriceBGR.height(),matriceBGR.cols(),matriceBGR.type());
        Imgproc.cvtColor(matriceBGR,matriceHSV,Imgproc.COLOR_BGR2HSV);
        return matriceHSV;

    }

    //convertit une matrice avec 3 canaux en un vecteur de 3 matrices monocanal
    public static Vector<Mat> splitHSVChannels(Mat input) {
        Vector<Mat> channels = new Vector<Mat>();
        Core.split(input, channels);
        return channels;
    }

    
    public static void afficheImage(String title, Mat img){
        MatOfByte matOfByte=new MatOfByte();
        Highgui.imencode(".png",img,matOfByte);
        byte[] byteArray=matOfByte.toArray();
        BufferedImage bufImage=null;
        try{
            InputStream in=new ByteArrayInputStream(byteArray);
            bufImage=ImageIO.read(in);
            JFrame frame=new JFrame();
            frame.setTitle(title);
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);

        }
        catch(Exception e){
            e.printStackTrace();
        }


    }
    
    //Methode qui permet de saturer les couleurs rouges a partir de 3 seuils
    public static Mat seuillage(Mat input, int seuilRougeOrange, int seuilRougeViolet,int seuilSaturation){
        // Decomposition en 3 cannaux HSV
        Vector<Mat> channels = splitHSVChannels(input);
        //creation d'un seuil
        Scalar rougeviolet = new Scalar(seuilRougeViolet);
        Scalar rougeorange = new Scalar(seuilRougeOrange);
        Scalar saturation = new Scalar(seuilSaturation);
        Mat rouges=new Mat();
        Mat rouges2 = new Mat();
        Mat sat = new Mat();
        Mat oupsi = new Mat();
        Mat finale = new Mat();
        //Comparaison et saturation des pixels dont la composante rouge est plus grande que le seuil rougeViolet
        Core.compare(channels.get(0), rougeviolet, rouges, Core.CMP_GT);
        Core.compare(channels.get(0),rougeorange, rouges2 , Core.CMP_LT);
        Core.compare(channels.get(1),saturation,sat , Core.CMP_GT);
        Core.bitwise_or(rouges,rouges2,oupsi);
        Core.bitwise_and(oupsi,sat,finale);
        //image saturée a retourner
        return finale;
    }





    //extraction des contours d'une image donnee
    public static List<MatOfPoint> ExtractContours(Mat input) {
        // Detecter les contours
        int thresh = 100;
        Mat canny_output = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        MatOfInt4 hierarchy = new MatOfInt4();
        Imgproc.Canny( input, canny_output, thresh, thresh*2);


        Imgproc.findContours( canny_output, contours, hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat drawing = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );
        Random rand = new Random();
        for( int i = 0; i< contours.size(); i++ )
        {
            Scalar color = new Scalar( rand.nextInt(255 - 0 + 1) , rand.nextInt(255 - 0 + 1),rand.nextInt(255 - 0 + 1) );
            Imgproc.drawContours( drawing, contours, i, color, 1, 8, hierarchy, 0, new Point() );
        }

        return contours;
    }

    //permet de decouper et identifier les contours carres, triangulaires ou rectangulaires.
    public static Mat DetectForm(Mat img,MatOfPoint contour) {
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        float[] radius = new float[1];
        Point center = new Point();
        Rect rect = Imgproc.boundingRect(contour);
        double contourArea = Imgproc.contourArea(contour);


        matOfPoint2f.fromList(contour.toList());
        // Cherche le plus petit cercle entourant le contour
        Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
        //System.out.println(contourArea+" "+Math.PI*radius[0]*radius[0]);
        //on dit que c'est un cercle si l'aire occupÃ© par le contour est a supÃ©rieure a  80% de l'aire occupÃ©e par un cercle parfait
        if ((contourArea / (Math.PI*radius[0]*radius[0])) >=0.5) {
            //System.out.println("Cercle");
            Core.circle(img, center, (int)radius[0], new Scalar(255, 0, 0), 2);
            Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
            Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
            Mat sign = Mat.zeros(tmp.size(),tmp.type());
            tmp.copyTo(sign);
            return sign;
        }else {

            Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
            long total = approxCurve.total();
            if (total == 3 ) { // is triangle
                //System.out.println("Triangle");
                Point [] pt = approxCurve.toArray();
                Core.line(img, pt[0], pt[1], new Scalar(255,0,0),2);
                Core.line(img, pt[1], pt[2], new Scalar(255,0,0),2);
                Core.line(img, pt[2], pt[0], new Scalar(255,0,0),2);
                Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
                Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
                Mat sign = Mat.zeros(tmp.size(),tmp.type());
                tmp.copyTo(sign);
                return null;
            }
            if (total >= 4 && total <= 6) {
                List<Double> cos = new ArrayList<>();
                Point[] points = approxCurve.toArray();
                for (int j = 2; j < total + 1; j++) {
                    cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
                }
                Collections.sort(cos);
                Double minCos = cos.get(0);
                Double maxCos = cos.get(cos.size() - 1);
                boolean isRect = total == 4 && minCos >= -0.1 && maxCos <= 0.3;
                boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
                if (isRect) {
                    double ratio = Math.abs(1 - (double) rect.width / rect.height);
                    //drawText(rect.tl(), ratio <= 0.02 ? "SQU" : "RECT");
                    //System.out.println("Rectangle");
                    Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
                    Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
                    Mat sign = Mat.zeros(tmp.size(),tmp.type());
                    tmp.copyTo(sign);
                    return null;
                }
                if (isPolygon) {
                    //System.out.println("Polygon");
                    //drawText(rect.tl(), "Polygon");
                }
            }
        }
        return null;

    }



    public static double angle(Point a, Point b, Point c) {
        Point ab = new Point( b.x - a.x, b.y - a.y );
        Point cb = new Point( b.x - c.x, b.y - c.y );
        double dot = (ab.x * cb.x + ab.y * cb.y); // dot product
        double cross = (ab.x * cb.y - ab.y * cb.x); // cross product
        double alpha = Math.atan2(cross, dot);
        return Math.floor(alpha * 180. / Math.PI + 0.5);
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
		
		// deuxieme maniere de faire le seuillage
		return threshold_img;
	}
	
//cette fonction renvoie un score de comparaison entre deux images
    public static double Similitude(Mat object,String signfile) {
    	double total = 0.0;
    	
    	Mat threshold_img = DetecterCercles(object);
		List<MatOfPoint> listContours = ExtractContours(threshold_img);
		
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		float[] radius = new float[1];
		Point center = new Point();
		for(int c=0;c<listContours.size();c++) {
			MatOfPoint contour = listContours.get(c);
			double contourArea = Imgproc.contourArea(contour);
			matOfPoint2f.fromList(contour.toList());
			Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
			if((contourArea/(Math.PI*radius[0]*radius[0]))>=0.8) {
				Core.circle(object, center, (int)radius[0], new Scalar(0,255,0),2);
			}
		}
		//main.ImShow("Detection des cercles rouges", m);
	
		// Reconnaissance balles_rouges
		for(int c=0; c<listContours.size();c++) {

			
			MatOfPoint contour = listContours.get(c);
			double contourArea = Imgproc.contourArea(contour);
			matOfPoint2f.fromList(contour.toList());
			Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
			if((contourArea/(Math.PI*radius[0]*radius[0]))>=0.8){
				Core.circle(object, center, (int)radius[0], new Scalar(0,255,0),2);
				Rect rect = Imgproc.boundingRect(contour);
				Core.rectangle(object,new Point(rect.x,rect.y),
						new Point(rect.x+rect.width,rect.y+rect.height),
						new Scalar(0,255,0),2);
				Mat tmp = object.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
				Mat ball = Mat.zeros(tmp.size(), tmp.type());
				tmp.copyTo(ball);
				//main.ImShow("Ball", ball);
				
				// Mise à l'echelle
				Mat sroadSign = Highgui.imread(signfile);
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
				
				//System.out.println(matchs.total());
				
				Mat matchedImage = new Mat(sroadSign.rows(),sroadSign.cols()*2,sroadSign.type());
				Features2d.drawMatches(sObject, objectKeypoints, sroadSign, signKeypoints, matchs, matchedImage);
				//main.ImShow("matchs", matchedImage);
				
				
				
				// Methode DescriptorMatcher
				List<DMatch> matchesList = matchs.toList();
				Double max_dist = 0.0;
				Double min_dist = 100.0;
		
				for (int i = 0; i < matchesList.size(); i++) {
				    Double dist = (double) matchesList.get(i).distance;
				    if (dist < min_dist)
				        min_dist = dist;
				    if (dist > max_dist)
				    max_dist = dist;
				}
		
				LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
				for (int i = 0; i < matchesList.size(); i++)  {  
				    if (matchesList.get(i).distance <= (3 * min_dist))
				    good_matches.addLast(matchesList.get(i));
				}
				float[] distances = new float[good_matches.size()];
				for(int i=0;i<good_matches.size();i++) {
					distances[i]=good_matches.get(i).distance;
				}
				double moyenne = 0;
				for (int i=0;i<distances.length; i++) {
					moyenne+=distances[i];
				}
				total = moyenne/distances.length;
				//System.out.println(total);
				//return total;
    }
			
}
		return total;
    }
    }
