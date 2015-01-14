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

/**
 *
 * @author Pars
 */
public class CameraControl implements Runnable {

    private static CameraControl instance = null;
    private Thread thread = null;
    private Process process = null;
    private int width;
    private int height;
    private int fps;
    private final Config conf;
    private final Logger logger;
    private String currentAction = "";

    private CameraControl() {
        this.conf = Config.getInstance();
        this.logger = Logger.getInstance();
    }

    public String startRaspistill(int width, int height, int fps) {
        try {
            Process p = Runtime.getRuntime().exec("pidof " + conf.getString("camera", "raspistill_process_command_name"));
            InputStream inputStream = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String pid = br.readLine();
            if (pid == null || pid.isEmpty()) {
                this.width = width;
                this.height = height;
                this.fps = fps;
                if (thread != null && thread.isAlive() && !thread.isInterrupted()) {
                    thread.interrupt();
                }
                currentAction = conf.getString("camera", "raspistill_process_command_name");
                Runtime.getRuntime().exec(conf.getString("nginx", "nginx_start"));
                thread = new Thread(this);
                thread.start();
                return "camera raspistill started";
            } else {
                return "camera raspistill already stared Process ID: " + pid;
            }
        } catch (IOException ex) {
            logger.log(ex.getMessage());
            return "camera raspistill start error: " + ex.getMessage();

        }

    }

    public String stopRaspistill() {
        try {
            if (process != null) {
                process.destroy();
            }
            if (thread != null && thread.isAlive() && !thread.isInterrupted()) {
                thread.interrupt();
            }

            Process p = Runtime.getRuntime().exec("pidof " + conf.getString("camera", "raspistill_process_command_name"));
            InputStream inputStream = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String pid = br.readLine();
            if (pid != null && !pid.isEmpty()) {
                Runtime.getRuntime().exec("kill " + pid);
                Runtime.getRuntime().exec("pkill -f " + conf.getString("camera", "raspistill_process_command_name"));
                Runtime.getRuntime().exec(conf.getString("nginx", "nginx_stop"));
                Runtime.getRuntime().exec("pkill -f nginx");
                return "camera raspistill stoped";
            } else {
                return "seems there is no camera raspistill prcess to stop!";
            }
        } catch (IOException ex) {
            logger.log(ex.getMessage());
            return "camera raspistill stop error: " + ex.getMessage();
        }
    }

    public String startStreaming(int width, int height, int fps) {
        try {
            Process p = Runtime.getRuntime().exec("pidof " + conf.getString("camera", "raspivid_process_command_name"));
            InputStream inputStream = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String pid = br.readLine();
            if (pid == null || pid.isEmpty()) {
                this.width = width;
                this.height = height;
                this.fps = fps;
                if (thread != null && thread.isAlive() && !thread.isInterrupted()) {
                    thread.interrupt();
                }
                currentAction = conf.getString("camera", "raspivid_process_command_name");
                thread = new Thread(this);
                thread.start();
                return "camera raspivid started";
            } else {
                return "camera raspivid already stared Process ID: " + pid;
            }
        } catch (IOException ex) {
            logger.log(ex.getMessage());
            return "camera raspivid start error: " + ex.getMessage();

        }

    }

    public String stopStreaming() {
        try {
            if (process != null) {
                process.destroy();
            }
            if (thread != null && thread.isAlive() && !thread.isInterrupted()) {
                thread.interrupt();
            }

            Process p = Runtime.getRuntime().exec("pidof " + conf.getString("camera", "raspivid_process_command_name"));
            InputStream inputStream = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String pid = br.readLine();
            if (pid != null && !pid.isEmpty()) {
                Runtime.getRuntime().exec("kill " + pid);
                Runtime.getRuntime().exec("pkill -f " + conf.getString("camera", "raspivid_process_command_name"));
                return "camera raspivid stoped";
            } else {
                return "seems there is no camera raspivid prcess to stop!";
            }
        } catch (IOException ex) {
            logger.log(ex.getMessage());
            return "camera raspivid stop error: " + ex.getMessage();
        }
    }

    public static CameraControl getInstance() {
        if (instance == null) {
            instance = new CameraControl();
        }
        return instance;
    }

    @Override
    public void run() {
        //run_command_as_another_user
        String cameraStreamCommand = null;
        try {
            Process p = Runtime.getRuntime().exec(conf.getString("main", "get_current_user_name_command"));
            InputStream inputStream = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String userName = br.readLine();
            String camStartComand = "";

            if (currentAction.equals(conf.getString("camera", "raspivid_process_command_name"))) {
                camStartComand = String.format(conf.getString("camera", "raspivid_command"), this.width, this.height, this.fps);
            } else {
                camStartComand = String.format(conf.getString("camera", "raspistill_command"), this.width, this.height, this.fps);
            }

            if (userName.equalsIgnoreCase("root")) {
                String runAsAnotherUser = conf.getString("main", "run_command_as_another_user");
                String camera_running_user_name = conf.getString("camera", "user_name");
                cameraStreamCommand = String.format(runAsAnotherUser, camStartComand, camera_running_user_name);
            } else {
                cameraStreamCommand = camStartComand;
            }
        } catch (IOException ex) {
            logger.log(ex.getMessage());

        }

        if (conf.getInt("main", "dev_mode") == 1) {
            logger.log("Camera Control Unit: " + cameraStreamCommand);
        }
        try {
            String[] cmd = {
                "/bin/sh",
                "-c",
                cameraStreamCommand};
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (Exception ex) {
            logger.log(ex.getMessage());
        }
    }
}
