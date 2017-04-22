package me.qcarver.xwatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class DirectoryInputStream extends InputStream {

    File folder = null;
    ArrayList<FileInputStream> streams = null;

    //FileNameFilter implementation
    public class XmlFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith("xml");
        }
    }

    public DirectoryInputStream(File folder) {
        this.folder = folder;
    }

    @Override
    public int read() throws IOException {
        //reading for the first time? get soemthign to read
        if (streams == null) {
            getStreams();
        }
        int rv;
        //reading after we have already finished all reading?
        if (streams.isEmpty()) {
            rv = -1;
        } //get the next char
        else {
            rv = streams.get(0).read();

            //the last char was read from a stream move on if we can
            if (rv == -1) {
                //close and remove the current stream
                streams.remove(0).close();
                //if there is another stream open it.
                if (streams.size() > 0) {
                    rv = streams.get(0).read();
                }
            }
        }
        return rv;
    }

    private void getStreams() {
        if (folder.isDirectory()) {
            streams = new ArrayList();
            File[] arrayOfFiles = folder.listFiles(new XmlFilter());
            for (int i = 0; i < arrayOfFiles.length; i++) {
                if (arrayOfFiles[i].isFile()) {
                    try {
                        streams.add(new FileInputStream(arrayOfFiles[i]));
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DirectoryInputStream.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
