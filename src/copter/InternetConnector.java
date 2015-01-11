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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pars
 */
public class InternetConnector implements Runnable {

    private static InternetConnector instance = null;
    private Thread thread = null;
    private Process process = null;

    private InternetConnector() {

    }

    public String connectBySakis3g() {
        if (this.checkConnectionToServer() || this.checkInternetConnection()) {
            return "device is already connected to the internet.";
        }
        try {
            String internetConnectProcessCommandName = Config.getInstance().getString("main", "internet_connect_process_command_name");
            Process p = Runtime.getRuntime().exec("pidof " + internetConnectProcessCommandName);
            InputStream inputStream = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String pid = br.readLine();
            if (pid == null || pid.isEmpty()) {
                thread = new Thread(this);
                thread.start();
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                return "connecting...";
            } else {
                return "connection is already lunched " + internetConnectProcessCommandName + " PID:" + pid;
            }
        } catch (IOException ex) {
            Logger.getLogger(CameraControl.class.getName()).log(Level.SEVERE, null, ex);
            return "connection error: " + ex.getMessage();
        }
    }

    public boolean checkConnectionToServer() {
        String serverHost = Config.getInstance().getString("main", "server_host");
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress(serverHost, 80);
        try {
            sock.connect(addr, 3000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
            }
        }
    }

    public boolean checkInternetConnection() {
        String site = "www.google.com";
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress(site, 80);
        try {
            sock.connect(addr, 3000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
            }
        }
    }

    public String disconnectFromSakis3g() {
        try {
            String internetConnectProcessCommandName = Config.getInstance().getString("main", "internet_connect_process_command_name");
            Process p = Runtime.getRuntime().exec("pidof " + internetConnectProcessCommandName);
            InputStream inputStream = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String pid = br.readLine();
            if (pid != null && !pid.isEmpty()) {
                String internetDisconnectCommand = Config.getInstance().getString("main", "internet_disconnect_command");
                Runtime.getRuntime().exec(internetDisconnectCommand);
                //Runtime.getRuntime().exec("kill " + pid);
                return "disconnecting...";
            } else {
                return "there is no connection prcess to stop!";
            }
        } catch (IOException ex) {
            Logger.getLogger(CameraControl.class.getName()).log(Level.SEVERE, null, ex);
            return "connection error: " + ex.getMessage();
        }
    }

    public static InternetConnector getInstance() {
        if (instance == null) {
            instance = new InternetConnector();
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            String[] cmd = {
                "/bin/sh",
                "-c",
                Config.getInstance().getString("main", "internet_connect_command")
            };
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
