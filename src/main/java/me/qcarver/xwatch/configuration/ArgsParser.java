package me.qcarver.xwatch.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.System.exit;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author qcarver
 */
public class ArgsParser extends Configuration {

    private Options options;
    boolean help = false;
    String[] args = null;
    static private ArgsParser instance = null;
    
    public static ArgsParser get(){
        return instance;
    }

    public static void main(String[] args) {

        String moreArgs[] = new String[args.length];//+1];
        //moreArgs[args.length]="-p";

        ArgsParser argsParser = new ArgsParser(args);//moreArgs);
        System.out.println("null statement");

    }

    public ArgsParser(String[] args) {
        this.args = args;
        makeOptions();
        parse();
        instance = this;
    }

    public Options getOptions() {
        return options;
    }

    @SuppressWarnings("static-access")
    private void makeOptions() {
        // create Options object
        options = new Options();

        Option srcDir = OptionBuilder.withArgName("srcDir")
                .hasArg()
                .withLongOpt("srcDir")
                .withDescription("the dir containing xml data to visualize ")
                .create("d");

        Option xmlRegEx = OptionBuilder.withArgName("xmlRegEx")
                .hasArg()
                .withLongOpt("xmlRegEx")
                .withDescription("the reg ex used to identify xml docs in a file stream ")
                .create("x");

        Option timeScale = OptionBuilder.withArgName("timeScale")
                .hasArg()
                .withLongOpt("timeScale")
                .withDescription("time scale in seconds, default is "
                        + this.timeScale)
                .create("t");

        Option colors = OptionBuilder.withArgName("colors")
                .hasArg()
                .withLongOpt("colors")
                .withDescription("colors that objects should use, default is rnd assigned")
                .create("c");

        Option width = OptionBuilder.withArgName("width")
                .hasArg()
                .withLongOpt("width")
                .withDescription("width of gui panel, default is: "
                        + panelWidth)
                .create("w");

        Option height = OptionBuilder.withArgName("height")
                .hasArg()
                .withLongOpt("height")
                .withDescription("height of gui panel, default is: "
                        + panelHeight)
                .create("h");

        Option props = OptionBuilder.withArgName("props")
                .hasArg()
                .withLongOpt("props")
                .withDescription("properties file to use default is: "
                        + propertiesFile)
                .create("p");

        Option saveProps = OptionBuilder.withArgName("saveProps")
                .withLongOpt("saveProps")
                .withDescription("write or update properties file with args")
                .create("u");

        Option help = new Option("help", "print this message");

        options.addOption(srcDir);
        options.addOption(xmlRegEx);
        options.addOption(timeScale);
        options.addOption(colors);
        options.addOption(width);
        options.addOption(height);
        options.addOption(props);
        options.addOption(saveProps);
    }

    private void parse() {

        CommandLineParser parser = new BasicParser();
        boolean srcSpecified = false;
        boolean xformSpecified = false;
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                throw new ParseException("help invoked");
            }
            //want to check this option first 
            if (line.hasOption("saveProps")) {
                savingProperties = true;
            }
            if (line.hasOption("props")) {
                propertiesFile = line.getOptionValue("props");
            }
            //get our default values
            readProperties();
            if (line.hasOption("srcDir")) {
                xmlDir = line.getOptionValue("srcDir");
            }
            if (line.hasOption("xmlRegEx")) {
                xmlRegEx = line.getOptionValue("xmlRegEx");
            }
            if (line.hasOption("timeScale")) {
                timeScale = Float.parseFloat(line.getOptionValue("timeScale"));
            }
            if (line.hasOption("colors")) {
                getColors(line.getOptionValue("colors"));
            }
            if (line.hasOption("width")) {
                panelWidth = Integer.parseInt(line.getOptionValue("width"));
            }
            if (line.hasOption("height")) {
                panelWidth = Integer.parseInt(line.getOptionValue("height"));
            }
            if (savingProperties) {
                writeProperties(propertiesFile);
            }
        } catch (ParseException e1) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("xwatch",
                    " [options] "
                    + "provides a visualizer for xml data in the srcDir.\n",
                    options,
                    "", true);
        }
    }

    private Map<String, String> parseStringDelimitedList(String stringDelimitedList) {
        Map<String, String> params = new HashMap<String, String>();
        //add back in the topics the user wants
        StringTokenizer st = new StringTokenizer(stringDelimitedList, ",");
        while (st.hasMoreTokens()) {
            String string = st.nextToken();
            if (string.contains("=")) {
                params.put(string.split("=")[0], string.split("=")[1]);
            } else {
                System.err.print("params must object=value pairs");
                exit(0);
            }
        }
        return params;
    }

    private void readProperties() {
        try {
            Properties props = new Properties();
            File myProperties = new File(propertiesFile);
            if (!myProperties.exists()) {
                if (savingProperties) {
                    writeProperties(propertiesFile);
                }
                throw new IllegalArgumentException("The "
                        + "properties file: " + System.getProperty("user.dir")
                        + propertiesFile
                        + ",  doesn't exist. If you would like to start a new"
                        + "one by this name, use the \"-u\" option");
            } else {
                InputStream input = new FileInputStream(myProperties);
                props.load(input);

                timeScale
                        = Float.parseFloat(props.getProperty("timeScale"));
                getColors(props.getProperty("colors"));
                panelWidth
                        = Integer.parseInt(props.getProperty("width"));
                panelHeight
                        = Integer.parseInt(props.getProperty("height"));
                xmlDir = props.getProperty("srcDir");
                xmlRegEx = props.getProperty("xmlRegEx");
            }
        } catch (IOException e) {

        }
    }

    private String getColorMapAsString() {
        String serialized = "";
        if ((colorMap != null) && (colorMap.keySet() != null)) {
            for (String objectName : colorMap.keySet()) {
                if (!serialized.isEmpty()) {
                    serialized += ",";
                }
                serialized += objectName + "="
                        + Integer.toString(
                                colorMap.get(objectName) & 0xFFFFFF, 16).
                        toUpperCase();
            }
        }
        return serialized;
    }
    
    public void writeProperties(){
        writeProperties(propertiesFile);
    }

    private void writeProperties(String fileName) {
        try {
            Properties props = new Properties();
            props.setProperty("srcDir", xmlDir);
            props.setProperty("xmlRegEx", xmlRegEx);
            props.setProperty("timeScale", String.valueOf(timeScale));
            props.setProperty("colors", getColorMapAsString());
            props.setProperty("width", String.valueOf(panelWidth));
            props.setProperty("height", String.valueOf(panelHeight));
            props.setProperty("saveProps", String.valueOf(savingProperties));
            File file = new File(fileName);
            OutputStream out = new FileOutputStream(file);

            props.store(out, "This is a properties file written for XWatch");
        } catch (Exception e) {
            System.err.println("Threw exception writing out a properties file");
            e.printStackTrace();
        }
    }

    void getColors(String topicsString) {
        //colors may already be initiated by properties file.. but maybe not
        if (colorMap == null) {
            colorMap = new HashMap<>();
        }

        //add back in the topics the user wants
        StringTokenizer st = new StringTokenizer(topicsString, ",");
        while (st.hasMoreTokens()) {
            String pair = "";
            try {
                //where pair is something like RedAlertFoo=FF0000
                pair = st.nextToken();
                //where this is (eg) "RedAlertFoo"
                String objectName = pair.split("=")[0];
                //where this is a decimal number (eg) FF0000
                int value = Integer.parseInt(
                        pair.split("=")[1].toUpperCase(), 16) & 0xFFFFFF;
                //add this object's color to the map
                colorMap.put(objectName, value);

            } catch (PatternSyntaxException e) {
                System.err.println("Couldn't parse object color pair. Object "
                        + "name and integer value for color should be seperated "
                        + "by a space eg: RedAlertFoo=16711680");
                exit(1);
            } catch (NumberFormatException e) {
                System.err.println("Couldn't parse object color pair. The "
                        + "expression to the right of the equals needs to "
                        + "be six uppercase hex digits (eg) FF000 is red");

            }
        }
    }
}
