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
import java.sql.*;

public class main extends JFrame {

    static boolean detecte = true;
    private JPanel contentPane;
    private JTextField imgFile;
    private JTextArea panneau;
    private JPanel panel_1 ;
    private static JPanel panel_2 ;
    private static Statement stmt;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
            	try {
            		Class.forName("com.mysql.jdbc.Driver");
                    Connection con=DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/twizzy","root","");//connection ? la base de donn?e
                    stmt=con.createStatement(); 
                  //initialisation des tables de la base de donn?es :
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS 32_descripteurs_points_interets (points_interets VARCHAR(20))");
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS point_interets_panneaux (panneau20 VARCHAR(20), panneau30 VARCHAR(20),panneau40 VARCHAR(20),panneau50 VARCHAR(20),panneau60 VARCHAR(20),panneau70 VARCHAR(20),panneau80 VARCHAR(20),panneau90 VARCHAR(20),panneau100 VARCHAR(20),panneau110 VARCHAR(20),panneau120 VARCHAR(20),panneau130 VARCHAR(20))");
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS panneaux_croisees (id_panneau INT PRIMARY KEY NOT NULL AUTO_INCREMENT, nom_panneau VARCHAR(20))");
            	}
            	catch (Exception err) {
                    err.printStackTrace();
            	}
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
        setBounds(50, 1, 1200, 700);						// ouvre la fen?tre avec sa taille
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));	// donne la taille des contours
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(Color.black);	// couleur du contour
        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 1164, 639);
        contentPane.add(panel);					// place les contours
        panel.setLayout(null);
        panel.setBackground(Color.blue);
        
        
        panel_1 = new JPanel();
        panel_1.setBounds(10, 130, 800, 500); //emplacement image a detecter
        panel.add(panel_1);
        panel_1.setBackground(Color.blue);

        
        panel_2 = new JPanel();
        panel_2.setBounds(900, 250, 254, 254); //emplacement panneau detecte
        panel.add(panel_2);
        panel_2.setBackground(Color.BLUE);
        
        
        // BOUTON PERMETTANT DE CHARGER UNE IMAGE
        JButton btnChargerImage = new JButton("Charger Image");
        btnChargerImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                Mat m=Highgui.imread(imgFile.getText(),Highgui.CV_LOAD_IMAGE_COLOR);
                panel_1.removeAll();			// retire une image de "panel_1" si il y en avait d?j? une
                panel_1.repaint();
                panel_1.add(new JLabel(new ImageIcon(Mat2bufferedImage(m))));
                validate();
            }
        });
        
        // PLACEMENT DU BOUTON "CHARGER IMAGE"
        btnChargerImage.setBounds(10, 10, 140, 30);
        panel.add(btnChargerImage);
		
        // PLACEMENT DE LA FENETRE PERMETTANT DE RENTRER LE NOM DE L'IMAGE OU DE LA VIDEO
        imgFile = new JTextField();
        imgFile.setBounds(10, 50, 140, 20);
        panel.add(imgFile);
        
        // BOUTON NIVEAU DE GRIS
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
        // PLACEMENT BOUTON NIVEAU DE GRIS
        btnNiveauGris.setBounds(1010, 10, 140, 30);
        panel.add(btnNiveauGris);
        
        // BOUTON MISE DE L'IMAGE EN HSV
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
        
        // PLACEMENT DU BOUTON HSV
        btnButtonHSV.setBounds(1010, 50, 140, 30);
        panel.add(btnButtonHSV);
        
        // BOUTON IMAGE SATUREE EN ROUGE
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
        
        // PLACEMENT DU BOUTON SATURATION ROUGE
        btnSaturationRouge.setBounds(1010, 90, 140, 30);
        panel.add(btnSaturationRouge);
        
        // BOUTON DETECTE CONTOURS
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
        
        // PLACEMENT DU BOUTON DETECTE CONTOURS
        btnContours.setBounds(1010, 130, 140, 30);
        panel.add(btnContours);
        
        // BOUTON DETECTION QUI NE FONCTIONNE PAS BIEN (NE DETECTE PAS LE BON PANNEAU)
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
									try {
										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 30')");
									} catch (SQLException e1) {
										e1.printStackTrace();
									}

                                          break;
                                      case 1:
                                          panneau.setText("Panneau 50 detecte");
                                          fileImg="ref50.jpg";
                                          try {
      										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 50')");
      									} catch (SQLException e1) {
      										e1.printStackTrace();
      									}
                                          break;
                                      case 2:
                                          panneau.setText("Panneau 70 detecte");
                                          fileImg="ref70.jpg";
                                          try {
      										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 70')");
      									} catch (SQLException e1) {
      										e1.printStackTrace();
      									}
                                          break;
                                      case 3:
                                          panneau.setText("Panneau 90 detecte");
                                          try {
      										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 90')");
      									} catch (SQLException e1) {
      										e1.printStackTrace();
      									}
                                          fileImg="ref90.jpg";
                                          break;
                                      case 4:
                                          panneau.setText("Panneau 110 detecte");
                                          fileImg="ref110.jpg";
                                          try {
      										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 110')");
      									} catch (SQLException e1) {
      										e1.printStackTrace();
      									}
                                          break;
                                      case 5:
                                          panneau.setText("Panneau interdiction de depasser detecte");
                                          fileImg="refdouble.jpg";
                                          try {
      										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau interdiction de depasser detecte')");
      									} catch (SQLException e1) {
      										e1.printStackTrace();
      									}
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
        
        // PLACEMENT DU MAUVAIS BOUTON DETECTION
        btnMatching.setBounds(170, 10, 140, 30);
        panel.add(btnMatching);

        //PLACEMENT DE LA FENETRE NOM DU PANNEAU DETECTE
        panneau = new JTextArea();
        panneau.setBounds(1010, 510, 140, 30);
        panel.add(panneau);
        panneau.setColumns(10);


        // BOUTON DETECTION QUI FONCTIONNE
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
                                            try {
          										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 30')");
          									} catch (SQLException e1) {
          										e1.printStackTrace();
          									}
                                            break;
                                        case 1:
                                            panneau.setText("Panneau 50 detecte");
                                            fileImg="ref50.jpg";
                                            try {
          										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 50')");
          									} catch (SQLException e1) {
          										e1.printStackTrace();
          									}
                                            break;
                                        case 2:
                                            panneau.setText("Panneau 70 detecte");
                                            fileImg="ref70.jpg";
                                            try {
          										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 70')");
          									} catch (SQLException e1) {
          										e1.printStackTrace();
          									}
                                            
                                            break;
                                        case 3:
                                            panneau.setText("Panneau 90 detecte");
                                            fileImg="ref90.jpg";
                                            try {
          										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 90')");
          									} catch (SQLException e1) {
          										e1.printStackTrace();
          									}
                                            break;
                                        case 4:
                                            panneau.setText("Panneau 110 detecte");
                                            fileImg="ref110.jpg";
                                            try {
          										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 110')");
          									} catch (SQLException e1) {
          										e1.printStackTrace();
          									}
                                            break;
                                        case 5:
                                            panneau.setText("Panneau interdiction de depasser detecte");
                                            fileImg="refdouble.jpg";
                                            try {
          										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau interdiction de depasser detecte')");
          									} catch (SQLException e1) {
          										e1.printStackTrace();
          									}
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
        
        // PLACEMENT DU BOUTON DETECTION QUI FONCTIONNE
        btnMatching2.setBounds(330, 10, 140, 30);
        panel.add(btnMatching2);

        // BOUTON VIDEO
        JButton btnVideo = new JButton("Lancer la video");
        btnVideo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                
                /// ATTENTION : ? changer pour chacun !!!
                System.load("C:\\Users\\anaar\\Downloads\\opencv\\build\\x64\\vc12\\bin\\opencv_ffmpeg2413_64.dll");
                
                MyThread thread = new MyThread(imgFile.getText(),panneau,panel_1);
                thread.start();
            }
        });
        
        // PLACEMENT DU BOUTON VIDEO
        btnVideo.setBounds(10, 80, 140, 30);
        panel.add(btnVideo);
	//*/
    }

    public static void LectureVideo(String nomVideo, JTextArea panneau, JPanel panel_1 ) {
    	
        Mat frame = new Mat();
        VideoCapture camera = new VideoCapture(nomVideo);
        
        while (camera.read(frame)) {
            if (detecte==true) {			// si on detecte un panneau, on arrete pdt 40 images
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
                    		//indexmax=identifiepanneau(objetrond); // identifie le panneau avec la d?tection 1 : ne marche pas
		                switch(indexmax){
		                    case -1:
		                        break;
		                    case 0:
		                        panneau.setText("Panneau 30 detecte");
		                        fileImg="ref30.jpg";
                                try {
										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 30')");
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
		                        break;
		                    case 1:
		                        panneau.setText("Panneau 50 detecte");
		                        fileImg="ref50.jpg";
                                try {
										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 50')");
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
		                        break;
		                    case 2:
                                try {
										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 70')");
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
		                        panneau.setText("Panneau 70 detecte");
		                        fileImg="ref70.jpg";
		                        break;
		                    case 3:
		                        panneau.setText("Panneau 90 detecte");
		                        fileImg="ref90.jpg";
		                        try {
										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 90')");
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
		                        break;
		                    case 4:
		                        panneau.setText("Panneau 110 detecte");
                                try {
										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau 110')");
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
		                        fileImg="ref110.jpg";
		                        break;
		                    case 5:
		                        panneau.setText("Panneau interdiction de depasser detecte");
                                try {
										stmt.executeUpdate("INSERT INTO panneaux_croisees (`nom_panneau` )VALUES ('Panneau interdiction de depasser ')");
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
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
            //panel_2.repaint();		// enlev? pour que les panneaux restent affich?s tant qu'un autre panneau n'est pas apparu
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
