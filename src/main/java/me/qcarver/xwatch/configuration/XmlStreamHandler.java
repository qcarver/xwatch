package me.qcarver.xwatch.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A simple sax parser class which just writes the root element to a file
 * @author qcarver
 */
public class XmlStreamHandler extends DefaultHandler {

    StringBuffer element1Content = null;
    String firstElementName = "";
    Boolean inFirstElement = false;
    File saveDir = null;

    public XmlStreamHandler(File saveDir) {
        this.saveDir = saveDir;
        reset();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atrbts) throws SAXException {
        if (firstElementName != null) {
            firstElementName = localName;
            inFirstElement = true;
            element1Content = new StringBuffer();
            System.out.println("Capturing new " + localName + " element ");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals(firstElementName)) {
            inFirstElement = false;
            File file;
            try {
                file = File.createTempFile(
                        "temp",
                        firstElementName + Long.toString(System.nanoTime()) + ".xml",
                        saveDir);
                BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
                bwr.write(element1Content.toString());
                bwr.flush();
                bwr.close();
                System.out.println("Wrote new " + localName + " element to file");
            } catch (IOException ex) {
                Logger.getLogger("ERROR: Could not create a temp file to store a "
                        + "streamed element in. Element cannot be streamed");
            }
            //reset for next doc in stream
            reset();
        }
    }

    private void reset() {
        firstElementName = null;
        inFirstElement = false;
        element1Content = null;
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        if (inFirstElement) {
            element1Content.append(chars);
        }
    }
}
