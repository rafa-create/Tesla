package Tweazy;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class video {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        /// ATTENTION : à changer pour chacun !!!
        System.load("C:\\Users\\anaar\\Downloads\\opencv\\build\\x64\\vc12\\bin\\opencv_ffmpeg2413_64.dll");
    }

    static Mat imag = null;
    static boolean detecte = false;

    public static void main(String[] args) {
        JFrame jframe = new JFrame("Detection de panneaux sur un flux video");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setSize(720, 480);
        jframe.setVisible(true);

        Mat frame = new Mat();
        VideoCapture camera = new VideoCapture("video1.avi");
        Mat PanneauAAnalyser = null;
        int i=0;
        while (camera.read(frame) && i<130) {
            //A completer
            if (detecte==true) {
                for (int j=0;j<10;j++) {
                    camera.read(frame);
                }
            }
            detecte=false;
            ImageIcon image = new ImageIcon(Mat2bufferedImage(frame));
            vidpanel.setIcon(image);
            Mat transformee=fonctions.BGRversHSV(frame);
            //la methode seuillage est ici extraite de l'archivage jar du meme nom
            Mat saturee=fonctions.seuillage(transformee, 6, 170, 110);
            Mat objetrond = null;

            //Creation d'une liste des contours a partir de l'image saturee
            List<MatOfPoint> ListeContours= fonctions.ExtractContours(saturee);
            //Pour tous les contours de la liste
            for (MatOfPoint contour: ListeContours){
            	//if(i==130) {
	                objetrond=fonctions.DetectForm(frame,contour);
	                int indexmax=identifiepanneau(objetrond);
	                switch(indexmax){
	                    case -1:break;
	                    case 0:System.out.println("Panneau 30 detecte");break;
	                    case 1:System.out.println("Panneau 50 detecte");break;
	                    case 2:System.out.println("Panneau 70 detecte");break;
	                    case 3:System.out.println("Panneau 90 detecte");break;
	                    case 4:System.out.println("Panneau 110 detecte");break;
	                    case 5:System.out.println("Panneau interdiction de depasser detecte");break;
	                }
	                if (indexmax>=0) {
	                    detecte=true;
	                }
	            }
            }
            vidpanel.repaint();
            i++;
        }
    //}


    public static BufferedImage Mat2bufferedImage(Mat image) {
        MatOfByte bytemat = new MatOfByte();
        Highgui.imencode(".jpg", image, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return img;
    }



    public static int identifiepanneau(Mat objetrond){
        double [] scores=new double [6];
        int indexmax=-1;
        if (objetrond!=null){
            scores[0]=fonctions.Similitude(objetrond,"ref30.jpg");
            scores[1]=fonctions.Similitude(objetrond,"ref50.jpg");
            scores[2]=fonctions.Similitude(objetrond,"ref70.jpg");
            scores[3]=fonctions.Similitude(objetrond,"ref90.jpg");
            scores[4]=fonctions.Similitude(objetrond,"ref110.jpg");
            scores[5]=fonctions.Similitude(objetrond,"refdouble.jpg");

            double scoremax=scores[0];

            for(int j=1;j<scores.length;j++){
                if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}
        }
        return indexmax;
    }
}
