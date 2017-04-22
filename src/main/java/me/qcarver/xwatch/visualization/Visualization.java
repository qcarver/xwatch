package me.qcarver.xwatch.visualization;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.qcarver.ballsack.Circle;
import me.qcarver.ballsack.XmlVisualization;
import me.qcarver.xwatch.configuration.Configuration;
import me.qcarver.xwatch.pubsub.Topic;

import processing.core.*;
import processing.event.MouseEvent;

/**
 * A simple Processing Demo Applet
 *
 * Used to demonstrate the combination of JFrame, JButton, JFileChooser and
 * PApplet
 *
 * Moves and displays a list of balls on the applet's screen A background image
 * can be loaded
 *
 * This applet can be used as ActionListener for Java Applications.
 *
 * @author georg munkel
 *
 */
public class Visualization extends PApplet implements ActionListener {

    //list of all balls
    ArrayList<DocumentVisualization> ballList;
    
    

    Topic topic = null;

    //list of colors
    Map<String, Color> objectColorMap = new HashMap<>();

    //the background image
    PImage bgImg = null;

    Set<File> knownFiles = new HashSet<>();

    File selectedDir = null;

    float scale = 1.0f;

    //FileTime lastFileTime = null;
    long numFiles = 0;

    public Visualization() {
        topic = new Topic();
        //mouseWheelCount = 0;
        scale = Configuration.get().getTimeScale();
        Circle.setPApplet(this);
    }

    @Override
    public void setup() {
        size(Configuration.get().getPanelWidth(), Configuration.get().getPanelHeight());
        ballList = new ArrayList<DocumentVisualization>();
        //creates a first ball
        //createNewBall();
                ellipseMode(RADIUS);
    }

    @Override
    public void draw() {

        //check if the background image is already loaded
        //if not, the background is painted white
        if (bgImg == null) {
            background(255);
        } else {
            image(bgImg, 0, 0, width, height);
        }

        //move and display all balls
        for (int i = 0; i < ballList.size(); i++) {
            DocumentVisualization ball = ballList.get(i);
            if (ball == null) {
                break;
            }
            ball.move();
            ball.display();
        }
        drawScaleKey();
        if (selectedDir != null) {
            findXmlFiles();
        }
    }

    void drawScaleKey() {
        //which is bigger x or y scale
        int maxDimen = (width > height) ? width : height;

        //divide out timescale to context size
        float radiusIncrement = maxDimen / (int) scale;

        //draw circles for each time division
        stroke(125);
        noFill();
        for (float radius = radiusIncrement;
                radius < maxDimen;
                radius += radiusIncrement) {
            ellipse(width / 2, height / 2, radius, radius);
        }

//            line (width/2, height/2, 0, height/2);
//            line (width/2, height/2, width/2, 0);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        scale = Configuration.get().getTimeScale();
        if (event.getCount() == 1) {
            scale = scale * 1.1f;
        } else if (event.getCount() == -1) {
            scale = scale / 1.1f;
            if (scale < 1) {
                scale = 1;
            }
        }

        System.out.println("timeScale = " + scale);

        Configuration.get().setTimeScale(scale);
        topic.postMessage(Configuration.get());
    }

    /**
     * implementation from interface ActionListener method is called from the
     * Application the String being compared is the ActionCommand from the
     * button
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals("create ball")) {
            //createNewBall();
        } else {
            println("actionPerformed(): can't handle " + evt.getActionCommand());
        }
    }

    /**
     * this method is called by the ActionListener asigned to the JButton
     * buttonLoad in Application
     */
    public void loadXmlDir(File selectedDir) {
        if (selectedDir.isDirectory()) {
            this.selectedDir = selectedDir;
            numFiles = 0;
            knownFiles = new HashSet<>();
        }
    }

    private void findXmlFiles() {
        if (selectedDir.list().length > numFiles) {
            List<File> files = new ArrayList(Arrays.asList(selectedDir.listFiles()));
            files.removeAll(knownFiles);
            Collections.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                }
            });
            FileTime prevTime = null;
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".xml")) {
                    FileTime time = null;
                    try {
                        time = Files.getLastModifiedTime(file.toPath());
                        if (prevTime == null) prevTime = time;
                    } catch (IOException ex) {
                        Logger.getLogger(Visualization.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                    long waitMillis = time.toMillis()-prevTime.toMillis();
                    createNewBall(file);
                    prevTime = time;
                }
            }
            knownFiles.addAll(files);
            numFiles = selectedDir.list().length;
        }
    }

    public void createFromString(String xmlData) {

    }

    /*
     * creates a new Ball instance and adds it to ballList
     */
    private void createNewBall(File file) {
        System.out.println("creating object for file: " + file.getName());
        
        DocumentVisualization nBall = new DocumentVisualization(
                this, file, topic);
        ballList.add(nBall);
        topic.register(nBall);
    }
}
