package me.qcarver.xwatch.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import me.qcarver.xwatch.configuration.Configuration;

/**
 *
 * @author qcarver
 */
public class XmlTools {

    //static (make conce) we are single thread and this util might get blasted
    private static XMLInputFactory xmlif = null;

    public String getRootNodeName(File file) {
        String nodeName = "unknown";
        try {
            Pattern xmlPattern = Pattern.compile(
                    //"<(?:(\\w*?)(?:\\sxmlns=\"[\\s\\S]*?\".+?)?>[\\s\\S]*?<\\/\\1>)+?");
                        //"<(?:(\\S*?)(?:\\sxmlns=\\\"\\S+\")?>[\\s\\S]*?<\\/\\1>)+");
                        "<(\\S*)");
                    String match = "";
            FileInputStream isr = new FileInputStream(file);
            Scanner scanner = new Scanner(isr);
            match = scanner.findWithinHorizon(xmlPattern, 0);
            if (match != null) {
                nodeName = scanner.match().group(1);
                System.out.println("found xml node named: " + nodeName);
            }
        } catch (Exception e) {
            System.out.println("Couldn't find root node name in file: "
                    + file.getAbsolutePath() + "\"" + nodeName + "\" will be "
                    + "used instead.");
        }
        return nodeName;
    }
}
