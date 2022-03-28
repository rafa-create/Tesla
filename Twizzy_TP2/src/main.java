import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

	// je fais un push
public class main {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Mat.eye(3, 3,CvType.CV_8UC1);
		//System.out.println("mat = "+mat.dump());
		
		Mat matr = lectureImage("opencv.png");
		// Lecture de l'image et retour de la matrice de l'image
		//System.out.println(matr);
		
		// Afichage du contenu de la matrice en mode texte avec des symboles
		//afficheMat(matr);
		
		/*// Affichage bgr
		Mat m = lectureImage("photo1.jpg");
		Vector<Mat> channels = new Vector<Mat>();
		Core.split(m, channels);
		// BGR order
		for(int i=0;i<channels.size();i++) {
			ImShow(Integer.toString(i),channels.get(i));
		}
		// BGR order : deuxième solution
		Mat dst = Mat.zeros(m.size(),m.type());
		Vector<Mat> chans = new Vector<Mat>();
		Mat empty = Mat.zeros(m.size(),CvType.CV_8UC1);
		for(int i=0;i<channels.size();i++) {
			ImShow(Integer.toString(i),channels.get(i));
			chans.removeAllElements();
			for(int j=0;j<channels.size();j++) {
				if(j!=i) {
					chans.add(empty);
				}else{
					chans.add(channels.get(i));
				}
			}
			Core.merge(chans, dst);
			ImShow(Integer.toString(i),dst);
		}
		*/
		
		
		/// PARTIE CONTOURS
		Mat m = main.lectureImage("panneau30.jpg");
		main.ImShow("Image normale", m);
		Mat hsv_image = Mat.zeros(m.size(),m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		//main.ImShow("HSV", hsv_image);
		List<MatOfPoint> contours = fonctions.DetecterContours(m);
		
		/// PARTIE SEUILLAGE
		fonctions.seuillage();
		
		// Seuillage 2
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat threshold_img = fonctions.DetecterCercles(m);
		main.ImShow("Cercles rouge", threshold_img);
		
		
		fonctions.reconnaissance_cercles_rouges();
		
	}
	
	public static Mat lectureImage(String fichier) {
		File f = new File(fichier);
		Mat m = Highgui.imread(f.getAbsolutePath());
		return m;
	}
	
	public static void afficheMat(Mat m) {
		for(int i=0;i<m.height();i++) {
			for(int j=0;j<m.height();j++) {
				double[] BGR = m.get(i, j);
				if(BGR[0]==255 && BGR[1]==255 && BGR[2]==255) {
					System.out.print(".");
				}else{
					System.out.print("+");
				}
			}
			System.out.println();
		}
	}
	
	public static void ImShow(String title, Mat img) {
		MatOfByte matOfByte = new MatOfByte();
		Highgui.imencode(".png", img, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);
			JFrame frame = new JFrame();
			frame.setTitle(title);
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.pack();
			frame.setVisible(true);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}





