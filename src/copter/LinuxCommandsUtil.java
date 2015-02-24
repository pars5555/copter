/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

/**
 *
 * @author default
 */
public class LinuxCommandsUtil {

    private static LinuxCommandsUtil instance = null;
    private static Logger logger = null;

    private LinuxCommandsUtil() {
        logger = Logger.getInstance();
    }

    public static LinuxCommandsUtil getInstance() {
        if (instance == null) {
            instance = new LinuxCommandsUtil();
        }
        return instance;
    }

    public String rebootSystem() {
        try {
            Runtime.getRuntime().exec(Config.getInstance().getString("main", "reboot_command"));
            return "System is now rebooting...";
        } catch (IOException ex) {
            logger.log(ex.getMessage());
            return ex.getMessage();
        }
    }

}
