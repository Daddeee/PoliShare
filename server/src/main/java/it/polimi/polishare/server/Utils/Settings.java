package it.polimi.polishare.peer.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private static final String confPath = "config.xml";
    private static final Properties properties;

    static {
        properties = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream(confPath);
            properties.loadFromXML(in);
            in.close();
        } catch (FileNotFoundException fnfEx) {
            System.err.println("Could not read properties from file " + confPath);
            fnfEx.printStackTrace();
            System.exit(1);
        } catch (IOException ioEx) {
            System.err.println("IOException encountered while reading from " + confPath);
            ioEx.printStackTrace();
            System.exit(1);
        }
    }

    private Settings() {
    }

    /**
     * @param property the searched property.
     * @return the string value of the given property saved in config file.
     */
    public static String getProperty(String property) {
        return properties.getProperty(property);
    }

    /**
     * Save the pair (name, value) as a property in the config file.
     *
     * @param name  the property name.
     * @param value the property value.
     */
    public static void storeProperty(String name, String value) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(confPath);
            properties.setProperty(name, value);
            properties.storeToXML(out, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
