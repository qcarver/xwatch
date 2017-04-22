package me.qcarver.xwatch.xml;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A simple xml FileFilter
 * @author qcarver
 */
public class XmlFileFilter implements FileFilter {
    private String discriminator;
    private XmlTools tools;
    //static (make conce) we are single thread and this util might get blasted
    private static XMLInputFactory xmlif = null;

    /**
     * constructor for use case where file is in xml with a specific root node
     * @param requiredRootNode the required type of root node name (not type)
     */
    public XmlFileFilter(String requiredRootNode) {
        this.discriminator = requiredRootNode;
    }
    
    /**
     * constructor for use case where file just has to be xml
     */
    public XmlFileFilter(){
        this.discriminator = null;
        tools = new XmlTools();
    }

    public boolean accept(File filename) {
        boolean match = false;
        //is this an xml file?
        if (filename.getName().toLowerCase().endsWith("xml")){
            //if user doesn't care what the root node is ..OR
            if ((discriminator == null) ||
                //the user does care and it matches what the user wants
               (tools.getRootNodeName(filename).contains(discriminator))) {
                    match = true;
                }            
        }
        return match;
    }

   

}
