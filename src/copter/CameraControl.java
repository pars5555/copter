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
import org.json.simple.JSONObject;

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
    private String cameraCommand = "";

    private CameraControl() {
        this.conf = Config.getInstance();
        this.logger = Logger.getInstance();
    }

    public String startStreamingRtmp(int width, int height, int fps) {
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
                cameraCommand = conf.getString("camera", "raspivid_rtmp_command");
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

    public String startHttpStreaming(int width, int height, int fps) {
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
                cameraCommand = conf.getString("camera", "raspivid_command");
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
                Runtime.getRuntime().exec("pkill vlc");
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
            camStartComand = String.format(cameraCommand, this.width, this.height, this.fps);

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

    public String doAction(JSONObject jsonParam) {
        if (!jsonParam.containsKey("action")) {
            return "Missing 'action' param!";
        }
        String action = (String) jsonParam.get("action");
        switch (action) {
            case Constants.CAMERA_START_HTTP_STREMING_ACTION:
                int width = (int) (long) jsonParam.get("width");
                int height = (int) (long) jsonParam.get("height");
                int fps = (int) (long) jsonParam.get("fps");
                return this.startHttpStreaming(width, height, fps);
            case Constants.CAMERA_START_RTMP_STREAMING_ACTION:
                int w = (int) (long) jsonParam.get("width");
                int h = (int) (long) jsonParam.get("height");
                int _fps = (int) (long) jsonParam.get("fps");
                return this.startStreamingRtmp(w, h, _fps);
            case Constants.CAMERA_STOP_STREAMING_COMMAND:
                return this.stopStreaming();
        }
        return "Unknown 'action' param: " + action;
    }
}
