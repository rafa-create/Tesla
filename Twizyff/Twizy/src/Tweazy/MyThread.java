package Tweazy;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.opencv.core.Core;

public class MyThread extends Thread {
    private String videoName;
    private JTextArea panneau;
    private JPanel panel_1;
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //System.loadLibrary("opencv_ffmpeg2413_64");
    }
    public MyThread(String videoName, JTextArea panneau, JPanel panel_1 )
    {
        this.videoName = videoName;
        this.panneau = panneau;
        this.panel_1 = panel_1;
        return;
    }

    @Override
    public void run() {
        Interface.LectureVideo(this.videoName,this.panneau,this.panel_1);
        return;
    }
}
