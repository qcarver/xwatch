package me.qcarver.xwatch.visualization;

import java.awt.Color;
import java.io.File;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;
import java.util.Random;
import me.qcarver.ballsack.XmlVisualization;
import me.qcarver.xwatch.xml.XmlTools;
import me.qcarver.xwatch.configuration.Configuration;
import me.qcarver.xwatch.pubsub.Observer;
import me.qcarver.xwatch.pubsub.Subject;
import processing.core.PApplet;

/**
 *
 * @author qcarver
 */
public class DocumentVisualization implements Observer {

    private Subject subject = null;
    Configuration config = null;
    long birthTime;
    float lifeSpan;
    float x;
    float y;
    float size;
    float speedX;
    float speedY;
    Color color;
    PApplet context;
    File objFile;
    String rootName;
    XmlTools tools;
    XmlVisualization xmlViz;
    double direction;
    
    DocumentVisualization(PApplet context, File file, Subject subject) {
        this.config = Configuration.get();
        oldTimeScale = this.config.getTimeScale();

        birthTime = getSeconds();

        this.objFile = file;
        if (tools == null) {
            tools = new XmlTools();
        }

        rootName = tools.getRootNodeName(file);

        Random random = new Random();
        direction = random.nextDouble();
        direction *= (2 * (Math.PI));

        lifeSpan = this.config.getTimeScale();
        //x = (context.width/2);
        //y = (context.height/2);

        this.context = context;
        //size =((context.width/10) * (file.length()/5000))+ 10;
        size = context.map(file.length(), 0, 675000, 10, context.width / 2) * 4;

        calcSpeed(this.config);
        if (this.config.getColorMap().containsKey(rootName)) {
            color = getColorFromNumber(config.getColorMap().get(rootName)
            );
        } else {
            color = new Color(context.random(1),
                    context.random(1), context.random(1));
            this.config.addColor(rootName, getNumberFromColor(color));
        }
        setSubject(subject);
        
        xmlViz = new XmlVisualization(file);
    }

    private float oldTimeScale = 1f;
    
    @Override
    public void update() {
        calcSpeed(config);
        lifeSpan = config.getTimeScale();
        //update scale
        System.err.println("timeScale transition?: " + oldTimeScale
                + "=>" + config.getTimeScale());
        //System.out.print("x was " + x + ", y was " + y);
        x = x * (oldTimeScale / config.getTimeScale());
        y = y * (oldTimeScale / config.getTimeScale());
        //System.out.println("x is " + x + ", y is " + y);
        oldTimeScale = config.getTimeScale();
    }

    @Override
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    private void calcSpeed(Configuration config) {
        //which is bigger x or y scale
        int maxDimen = (context.width > context.height)
                ? context.width : context.height;

        speedX = (1.0f / context.frameRate)
                * (maxDimen / 2.0f) / config.getTimeScale();

        speedY = speedX;
    }

    public void move() {
        x += speedX * cos(direction);
        y += speedY * sin(direction);

    }

    public void display() {
        if (inBounds()) {
            long time = getSeconds();
            float lifeLeft = birthTime + (long) lifeSpan - time;
            if (lifeLeft < 0) {
                lifeLeft = 0;
            }
            float alpha = context.map(lifeLeft, 0, lifeSpan, 0, 255);
            if (alpha < 0) {
                alpha = 0;
            }
            alpha = 255;
            context.stroke(color.getRGB(), alpha * 2);
            context.fill(color.getRGB(), alpha);
            //context.ellipse(x + (context.width / 2), y + (context.height / 2), size, size);
            
            xmlViz.getCircle().draw(x + (context.width / 2), y + (context.height / 2), size);

            //xmlViz.getCircle().update(x + (context.width / 2), y + (context.height / 2));//, size);
            //xmlViz.getCircle().draw();
        }
    }

    public long getSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    private Color getColorFromNumber(Integer rgbNum) {
        int red = rgbNum & 0xFF0000;
        red = red >> 16;
        int green = rgbNum & 0x00FF00;
        green = green >> 8;
        int blue = rgbNum & 0x0000FF;
        return new Color(red, green, blue);
    }

    private int getNumberFromColor(Color color) {
        int rgbNum = color.getRed();
        rgbNum = rgbNum << 8;
        rgbNum |= color.getGreen();
        rgbNum = rgbNum << 8;
        rgbNum |= color.getBlue();
        return rgbNum;
    }

    private boolean inBounds() {
        return (((x + (context.width / 2) + size / 2) < 0)
                || ((x + (context.width / 2) - size / 2) > context.width)
                || ((y + (context.height / 2) + size / 2) < 0)
                || ((y + (context.height / 2) - size / 2) > context.height))
                        ? false : true;
    }

}
