package me.qcarver.xwatch.xml;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author qcarver
 */
public class XmlDirFileFilter  extends FileFilter{
	/**
	 * accept directories which contain xml
	 */
	@Override
	public boolean accept(File f) {
            boolean value = false;
            if (f.isDirectory()){
                for (File file : f.listFiles()){
                    if(file.getName().toLowerCase().endsWith(".xml")){
                        value = true;
                    } 
                }
            }        
            return true;
        }

    @Override
    public String getDescription() {
        return "XmlDirectories";
    }
}
