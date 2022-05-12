package Tweazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class main extends JFrame {

    static boolean detecte = true;
    private JPanel contentPane;
    private JTextField imgFile;
    private JTextArea panneau;
    private JPanel panel_1 ;
    private static JPanel panel_2 ;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    main frame = new main();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(50, 1, 1200, 700);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(Color.lightGray);
        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 1164, 639);
        contentPane.add(panel);
        panel.setLayout(null);

        panel_1 = new JPanel();
        panel_1.setBounds(274, 46, 880, 582); //emplacement image a detecter
        panel.add(panel_1);

        panel_2 = new JPanel();
        panel_2.setBounds(10, 250, 254, 254); //emplacement panneau detecte
        panel.add(panel_2);

        JButton btnChargerImage = new JButton("Charger Image");
        btnChargerImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                Mat m=Highgui.imread(imgFile.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
                panel_1.removeAll();
                panel_1.repaint();
                panel_1.add(new JLabel(new ImageIcon(Mat2bufferedImage(m))));
                validate();
            }
        });

        btnChargerImage.setBounds(10, 10, 140, 20);
        panel.add(btnChargerImage);

        imgFile = new JTextField();
        imgFile.setBounds(155, 10, 140, 20);
        panel.add(imgFile);

        JButton btnNiveauGris = new JButton("Niveau de gris");
        btnNiveauGris.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                Mat m=Highgui.imread(imgFile.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
                //Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement a la taille du panneau de rÃ©ference
                Mat grayObject = new Mat(m.rows(), m.cols(), m.type());
                Imgproc.cvtColor(m, grayObject, Imgproc.COLOR_BGRA2GRAY);
                panel_1.removeAll();
                panel_1.repaint();
                panel_1.add(new JLabel(new ImageIcon(Mat2bufferedImage(grayObject))));
                validate();
            }
        });
        btnNiveauGris.setBounds(10, 45, 140, 20);
        panel.add(btnNiveauGris);

        JButton btnButtonHSV = new JButton("HSV");
        btnButtonHSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

                Mat imageOriginale=Highgui.imread(imgFile.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
                Mat imageTransformee=fonctions.BGRversHSV(imageOriginale);
                //Mat imageSatureExemple=MaBibliothequeTraitementImage.seuillage_exemple(imageTransformee, 170);
                //Mat imageSaturee=MaBibliothequeTraitementImage.seuillage(imageTransformee, 6, 170, 110);
                //fonctions.afficheImage("Image HSV", imageTransformee);
                panel_1.removeAll();
                panel_1.repaint();
                panel_1.add(new JLabel(new ImageIcon(Mat2bufferedImage(imageTransformee))));
                validate();
            }
        });
        btnButtonHSV.setBounds(10, 80, 140, 20);
        panel.add(btnButtonHSV);

        JButton btnSaturationRouge = new JButton("Saturation Rouge");
        btnSaturationRouge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

                Mat imageOriginale=Highgui.imread(imgFile.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
                Mat imageTransformee=fonctions.BGRversHSV(imageOriginale);
                Mat imageSaturee=fonctions.seuillage(imageTransformee, 6, 170, 110);
                panel_1.removeAll();
                panel_1.repaint();
                panel_1.add(new JLabel(new ImageIcon(Mat2bufferedImage(imageSaturee))));
                validate();
            }
        });
        btnSaturationRouge.setBounds(10, 115, 140, 20);
        panel.add(btnSaturationRouge);

        JButton btnContours = new JButton("Detecte contours");
        btnContours.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panneau.setText("");
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

                Mat imageOriginale=Highgui.imread(imgFile.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
                Mat imageTransformee=fonctions.BGRversHSV(imageOriginale);
                Mat input=fonctions.seuillage(imageTransformee, 6, 170, 110);
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
                panel_1.removeAll();
                panel_1.repaint();
                panel_1.add(new JLabel(new ImageIcon(Mat2bufferedImage(drawing))));
                validate();

            }
        });
        btnContours.setBounds(10, 150, 140, 20);
        panel.add(btnContours);

        JButton btnMatching = new JButton("Detection");
        btnMatching.addActionListener(new ActionListener() {
                      public void actionPerformed(ActionEvent e) {
                          String fileImg = "";
                          //Ouverture le l'image et saturation des rouges
                          System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                          Mat m=Highgui.imread(imgFile.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
                          //fonctions.afficheImage("Image testee", m);
                          Mat transformee=fonctions.BGRversHSV(m);
                          //la methode seuillage est ici extraite de l'archivage jar du meme nom
                          Mat saturee=fonctions.seuillage(transformee, 6, 170, 90);
                          Mat objetrond = null;

                          //Creation d'une liste des contours a partir de l'image saturee
                          List<MatOfPoint> ListeContours= fonctions.ExtractContours(saturee);
                          double [] scores=new double [6];
                          //Pour tous les contours de la liste
                          for (MatOfPoint contour: ListeContours  ){
                              objetrond=fonctions.DetectForm(m,contour);
                              

                              if (objetrond!=null){
                            	  //ImShow("bub",objetrond);
                                  scores[0]=fonctions.Similitude(objetrond,"ref30.jpg");
                                  scores[1]=fonctions.Similitude(objetrond,"ref50.jpg");
                                  scores[2]=fonctions.Similitude(objetrond,"ref70.jpg");
                                  scores[3]=fonctions.Similitude(objetrond,"ref90.jpg");
                                  scores[4]=fonctions.Similitude(objetrond,"ref110.jpg");
                                  scores[5]=fonctions.Similitude(objetrond,"refdouble.jpg");


                                  //recherche de l'index du maximum et affichage du panneau detecte
                                  double scoremax=-1;
                                  int indexmax=0;
                                  for(int j=0;j<scores.length;j++){
                                      if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}
                                  if(scoremax<0){System.out.println("Aucun Panneau detecte");}
                                  else{switch(indexmax){

                                      case -1:;break;
                                      case 0:
                                          panneau.setText("Panneau 30 detecte");
                                          fileImg="ref30.jpg";
                                          break;
                                      case 1:
                                          panneau.setText("Panneau 50 detecte");
                                          fileImg="ref50.jpg";
                                          break;
                                      case 2:
                                          panneau.setText("Panneau 70 detecte");
                                          fileImg="ref70.jpg";
                                          break;
                                      case 3:
                                          panneau.setText("Panneau 90 detecte");
                                          fileImg="ref90.jpg";
                                          break;
                                      case 4:
                                          panneau.setText("Panneau 110 detecte");
                                          fileImg="ref110.jpg";
                                          break;
                                      case 5:
                                          panneau.setText("Panneau interdiction de depasser detecte");
                                          fileImg="refdouble.jpg";
                                          break;
                                  }
                                  }

                              }
                          }
                          ImageIcon IMAGE = new ImageIcon(Toolkit.getDefaultToolkit().createImage(fileImg));
                          panel_2.removeAll();
                          panel_2.repaint();
                          panel_2.add(new JLabel(IMAGE));
                          validate();
                      }
                  }
        );
        btnMatching.setBounds(10, 181, 140, 20);
        panel.add(btnMatching);

        panneau = new JTextArea();
        panneau.setBounds(10, 510, 140, 20);
        panel.add(panneau);
        panneau.setColumns(10);


        ///detection 2
        JButton btnMatching2 = new JButton("Detection2");
        btnMatching2.addActionListener(new ActionListener() {
                          public void actionPerformed(ActionEvent e) {
                              String fileImg = "";
                              //Ouverture le l'image et saturation des rouges
                              System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                              Mat m=Highgui.imread(imgFile.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
                              //fonctions.afficheImage("Image testee", m);
                              Mat transformee=fonctions.BGRversHSV(m);
                              //la methode seuillage est ici extraite de l'archivage jar du meme nom
                              Mat saturee=fonctions.seuillage(transformee, 6, 170, 90);
                              Mat objetrond = null;

                              //Creation d'une liste des contours a partir de l'image saturee
                              List<MatOfPoint> ListeContours= fonctions.ExtractContours(saturee);
                              double [] scores=new double [6];
                              //Pour tous les contours de la liste
                              for (MatOfPoint contour: ListeContours  ){
                                  objetrond=fonctions.DetectForm(m,contour);

                                  if (objetrond!=null){
                                	//ImShow("bub",objetrond);

                                    BufferedImage img1 = Mat2bufferedImage(objetrond);
                                    
                      				//ImShow("bub",objetrond);
                      				
                                    scores[0]=fonctions.Similitude2(img1,"ref30.jpg");
                                    scores[1]=fonctions.Similitude2(img1,"ref50.jpg");
                                    scores[2]=fonctions.Similitude2(img1,"ref70.jpg");
                                    scores[3]=fonctions.Similitude2(img1,"ref90.jpg");
                                    scores[4]=fonctions.Similitude2(img1,"ref110.jpg");
                                    scores[5]=fonctions.Similitude2(img1,"refdouble.jpg");

                                    //recherche de l'index du maximum et affichage du panneau detecte
                                    double scoremax=-1;
                                    int indexmax=0;
                                    for(int j=0;j<scores.length;j++){
                                        if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}
                                    if(scoremax<0){System.out.println("Aucun Panneau detecte");}
                                    else{switch(indexmax){
                                        case -1:;break;
                                        case 0:
                                            panneau.setText("Panneau 30 detecte");
                                            fileImg="ref30.jpg";
                                            break;
                                        case 1:
                                            panneau.setText("Panneau 50 detecte");
                                            fileImg="ref50.jpg";
                                            break;
                                        case 2:
                                            panneau.setText("Panneau 70 detecte");
                                            fileImg="ref70.jpg";
                                            break;
                                        case 3:
                                            panneau.setText("Panneau 90 detecte");
                                            fileImg="ref90.jpg";
                                            break;
                                        case 4:
                                            panneau.setText("Panneau 110 detecte");
                                            fileImg="ref110.jpg";
                                            break;
                                        case 5:
                                            panneau.setText("Panneau interdiction de depasser detecte");
                                            fileImg="refdouble.jpg";
                                            break;
                                    	}
                                    }

                                  }
                              }
                              ImageIcon IMAGE = new ImageIcon(Toolkit.getDefaultToolkit().createImage(fileImg));
                              panel_2.removeAll();
                              panel_2.repaint();
                              panel_2.add(new JLabel(IMAGE));
                              validate();
                          }
                      }
        );
        btnMatching2.setBounds(10, 220, 140, 20);
        panel.add(btnMatching2);

        panneau = new JTextArea();
        panneau.setBounds(10, 510, 140, 20);
        panel.add(panneau);
        panneau.setColumns(10);
        //Bouton Video 
        JButton btnVideo = new JButton("Lancer la video");
        btnVideo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                
                /// ATTENTION : � changer pour chacun !!!
                System.load("C:\\Users\\anaar\\Downloads\\opencv\\build\\x64\\vc12\\bin\\opencv_ffmpeg2413_64.dll");
                
                //System.loadLibrary("opencv_ffmpeg2413_64");
                MyThread thread = new MyThread(imgFile.getText(),panneau,panel_1);
                thread.start();
            }
        });
        btnVideo.setBounds(300, 11, 140, 20);
        panel.add(btnVideo);

    }

    public static void LectureVideo(String nomVideo, JTextArea panneau, JPanel panel_1 ) {
    	
        Mat frame = new Mat();
        VideoCapture camera = new VideoCapture(nomVideo);
        
        // va lire la vid�o si le nom est le bon
        while (camera.read(frame)) {
            if (detecte==true) {
                for(int j=0;j<40;j++) {
                    camera.read(frame);
                }
            }
            detecte=false;
            
            String fileImg = "";

            panel_1.removeAll();

            panel_1.add(new JLabel(new ImageIcon(Mat2bufferedImage(frame))));
            panel_1.repaint();
            panel_1.validate();



            Mat transformee=fonctions.BGRversHSV(frame);
            //la methode seuillage est ici extraite de l'archivage jar du meme nom
            Mat saturee=fonctions.seuillage(transformee, 6, 170, 90);
            Mat objetrond = null;
            //Creation d'une liste des contours a partir de l'image saturee
            List<MatOfPoint> ListeContours= fonctions.ExtractContours(saturee);
            double [] scores=new double [6];
            for (MatOfPoint contour: ListeContours){
                objetrond=fonctions.DetectForm(frame,contour);
                
                if (objetrond!=null){
                	//ImShow("bub",objetrond);

                    BufferedImage img1 = Mat2bufferedImage(objetrond);
                    
      				//ImShow("bub",objetrond);
      				
                    scores[0]=fonctions.Similitude2(img1,"ref30.jpg");
                    scores[1]=fonctions.Similitude2(img1,"ref50.jpg");
                    scores[2]=fonctions.Similitude2(img1,"ref70.jpg");
                    scores[3]=fonctions.Similitude2(img1,"ref90.jpg");
                    scores[4]=fonctions.Similitude2(img1,"ref110.jpg");
                    scores[5]=fonctions.Similitude2(img1,"refdouble.jpg");

                    //recherche de l'index du maximum et affichage du panneau detecte
                    double scoremax=-1;
                    int indexmax=0;
                    for(int j=0;j<scores.length;j++){
                        if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}
                    if(scoremax<0){System.out.println("Aucun Panneau detecte");}
                    else {
                    		//indexmax=identifiepanneau(objetrond); // identifie le panneau avec la d�tection 1 : ne marche pas
		                switch(indexmax){
		                    case -1:
		                        break;
		                    case 0:
		                        panneau.setText("Panneau 30 detecte");
		                        fileImg="ref30.jpg";
		                        break;
		                    case 1:
		                        panneau.setText("Panneau 50 detecte");
		                        fileImg="ref50.jpg";
		                        break;
		                    case 2:
		                        panneau.setText("Panneau 70 detecte");
		                        fileImg="ref70.jpg";
		                        break;
		                    case 3:
		                        panneau.setText("Panneau 90 detecte");
		                        fileImg="ref90.jpg";
		                        break;
		                    case 4:
		                        panneau.setText("Panneau 110 detecte");
		                        fileImg="ref110.jpg";
		                        break;
		                    case 5:
		                        panneau.setText("Panneau interdiction de depasser detecte");
		                        fileImg="refdouble.jpg";
		                        break;
		                }
		                if (indexmax>=0) {
		                    detecte=true;
		                }
		            }
            }
            }
            panel_2.removeAll();
            //panel_2.repaint();		// enlev� pour que les panneaux restent affich�s tant qu'un autre panneau n'est pas apparu
            panel_2.add(new JLabel(new ImageIcon(fileImg)));
            panel_2.validate();
        }
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

    public static BufferedImage Mat2bufferedImage(Mat image) {
        MatOfByte bytemat = new MatOfByte();
        Highgui.imencode(".jpg", image, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }
}
