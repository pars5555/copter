/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import copter.interfaces.LastError;
import java.io.File;
import org.ini4j.Wini;

/**
 *
 * @author Pars
 */
public class Config implements LastError {

    private static Config instance;
    private String lastError;
    private Wini ini = null;

    private Config() {
        this.loadIniSettings();

    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    /**
     * Compute the absolute file path to the jar file. The framework is based on
     * http://stackoverflow.com/a/12733172/1614775 But that gets it right for
     * only one of the four cases.
     *
     * @param aclass A class residing in the required jar.
     *
     * @return A File object for the directory in which the jar file resides.
     * During testing with NetBeans, the result is ./build/classes/, which is
     * the directory containing what will be in the jar.
     */
    private String getJarDir() {
        File f = new File(System.getProperty("java.class.path"));
        File dir = f.getAbsoluteFile().getParentFile();
        return dir.toString();
    }

    private boolean loadIniSettings() {
        String jarDir = this.getJarDir();
        try {
            ini = new Wini(new File(jarDir + "/config.ini"));
        } catch (Exception ex) {
            lastError = ex.getMessage();
            return false;
        }
        return true;
    }

    public int getInt(String section, String name) {
        return ini.get(section, name, int.class);
    }

    public String getString(String section, String name) {
        return ini.get(section, name, String.class);
    }

    public Double getDouble(String section, String name) {
        return ini.get(section, name, double.class);
    }

    @Override
    public String getLastError() {
        return lastError;
    }

}
