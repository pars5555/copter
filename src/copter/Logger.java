/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Level;

/**
 *
 * @author default
 */
public class Logger {

    private static Logger instance;
    private final Config conf;
    private final File fout;
    private String logFile = null;

    private Logger() {
        this.conf = Config.getInstance();
        String jarDir = getJarDir();
        logFile = jarDir + "/log.txt";
        fout = new File(logFile);
        // if file doesnt exists, then create it

        try {
            if (!fout.exists()) {
                fout.createNewFile();
            }
            PrintWriter pr = new PrintWriter(fout);
            pr.write("");
            pr.close();
        } catch (Exception ex) {
        }
    }

    private String getJarDir() {
        File f = new File(System.getProperty("java.class.path"));
        File dir = f.getAbsoluteFile().getParentFile();
        return dir.toString();
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        if (conf.getInt("main", "dev_mode") != 1) {
            return;
        }
        System.out.println(message);
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
            out.println(message);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }
}
