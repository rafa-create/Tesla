

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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
		//main.ImShow("Contours", drawing);
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
	
	public static void seuillage(Mat m) {
		Mat hsv_image = Mat.zeros(m.size(),m.type());
		Imgproc.cvtColor(m, hsv_image,Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = new Mat();
		Core.inRange(hsv_image, new Scalar(0,100,100), new Scalar(10,255,255), threshold_img);
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9,9), 2,2);
		
		/* affiche en noir et blanc le contours rouge du panneau
		on aura besoin de la fonction après pour extraire le panneau 
		de l'image mais pas besoin de l'afficher */
		
		
		//main.ImShow("Cercles rouge", threshold_img);
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
	
	public static double reconnaissance_cercles_rouges(Mat m,String signfile) {
		double total = 0.0;
		Mat threshold_img = DetecterCercles(m);
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
		//main.ImShow("Detection des cercles rouges", m);
	
		// Reconnaissance balles_rouges
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
				//main.ImShow("Ball", ball);
				
				// Mise à l'échelle
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
				
				
				
				// Méthode DescriptorMatcher
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
				
		
			}
		}	
		return total;
	}
    public static void identifiepanneau(Mat objetrond){
        double [] scores=new double [6];
        int indexmax=-1;
        if (objetrond!=null){
            scores[0]=reconnaissance_cercles_rouges(objetrond,"panneau30.jpg");
            scores[1]=reconnaissance_cercles_rouges(objetrond,"panneau50.jpg");
            scores[2]=reconnaissance_cercles_rouges(objetrond,"panneau70.jpg");
            scores[3]=reconnaissance_cercles_rouges(objetrond,"panneau90.jpg");
            scores[4]=reconnaissance_cercles_rouges(objetrond,"panneau110.jpg");
            scores[5]=reconnaissance_cercles_rouges(objetrond,"panneau_interdiction_doubler.jpg");
            double scoremax=scores[0];
            for(int j=1;j<scores.length;j++){
                if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}
            if(scoremax<0){System.out.println("Aucun Panneau detecté");}
            else{switch(indexmax){

                case -1:;break;
                case 0:
                	System.out.println("Panneau 30 detecté");
                 
                    break;
                case 1:
                	System.out.println("Panneau 50 detecté");
                    
                    break;
                case 2:
                	System.out.println("Panneau 70 detecté");
                    
                    break;
                case 3:
                	System.out.println("Panneau 90 detecté");
                    
                    break;
                case 4:
                	System.out.println("Panneau 110 detecté");
                    
                    break;
                case 5:
                	System.out.println("Panneau interdiction de dépasser detecté");
                    
                    break;
            }
            }

        }
    }

}
