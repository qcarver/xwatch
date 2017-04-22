package me.qcarver.xwatch.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author qcarver
 */
public class Configuration {

    protected String xmlDir = "C:\\Users\\qcarver\\Desktop\\JPELT-2.1\\20151001Mediation\\AbaData";
    protected String xmlRegEx = "<(\\w*?)[\\s\\S]*?<\\/\\1>";
    protected float timeScale = 10.0f;
    protected Map<String, Integer> colorMap = new HashMap<>();
    protected int panelWidth = 400;
    protected int panelHeight = 400;
    protected String propertiesFile = "./xwatch.properties";
    protected boolean savingProperties;
    private static Configuration instance = null;

    /**
     * Configuration is a singleton, this method gets THE instance
     *
     * @return the instance of the Configuration
     */
    public static Configuration get() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    /**
     * hidden constructor.. use get()
     */
    protected Configuration() {
        instance = this;
    }

    ;
    
    
    /**
     * C
     * @return 
     */
    public boolean isSavingProperties() {
        return savingProperties;
    }

    public String getXmlDir() {
        return xmlDir;
    }

    /**
     * get the time scale in seconds
     *
     * @return
     */
    public float getTimeScale() {
        return timeScale;
    }

    public Map<String, Integer> getColorMap() {
        return colorMap;
    }

    public int getPanelWidth() {
        return panelWidth;
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public String getRegEx() {
        return xmlRegEx;
    }

    public void setTimeScale(float timeScale) {
        this.timeScale = timeScale;
    }

    public void addColor(String name, int color) {
        //if the color isn't already in the map
        if (!colorMap.containsKey(name)) {
            colorMap.put(name, color);
            if (savingProperties) {
                ArgsParser.get().writeProperties();
            }
        }

    }

}
