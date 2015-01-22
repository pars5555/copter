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

/**
 *
 * @author Pars
 */
public class InternetConnector implements Runnable {

    private static InternetConnector instance = null;
    private Thread thread = null;
    private Process process = null;
    private static Logger logger = null;

    private InternetConnector() {
        logger = Logger.getInstance();
    }

    public boolean connectBySakis3g() {
        if (this.checkInternetConnection()) {
            logger.log("device is already connected to the internet.");
            return true;
        }
        try {
            this.silentDisconectFromSakis3g();
            String internetConnectProcessCommandName = Config.getInstance().getString("main", "internet_connect_process_command_name");
            Process p = Runtime.getRuntime().exec("pidof " + internetConnectProcessCommandName);
            InputStream inputStream = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String pid = br.readLine();
            if (pid == null || pid.isEmpty()) {
                if (thread != null && thread.isAlive() && !thread.isInterrupted()) {
                    thread.interrupt();
                }
                thread = new Thread(this);
                thread.start();
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    logger.log(ex.getMessage());
                    return false;
                }
                logger.log("connecting...");
                return true;
            } else {
                logger.log("connection is already lunched " + internetConnectProcessCommandName + " PID:" + pid);
                return false;
            }
        } catch (IOException ex) {
            logger.log(ex.getMessage());
            return false;
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

    private String silentDisconectFromSakis3g() {
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
                Runtime.getRuntime().exec("kill " + pid);
                return "ok";
            } else {
                return "none";
            }
        } catch (IOException ex) {
            logger.log(ex.getMessage());
            return ex.getMessage();
        }
    }

    public String disconnectFromSakis3g() {
        String res = this.silentDisconectFromSakis3g();
        if (res!= null && res.equals("ok")) {
            return "disconnecting...";
        } else if (res!= null && res.equals("none")) {
            return "there is no connection prcess to stop!";
        } else {
            return "connection error: " + res;
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
            logger.log(ex.getMessage());
        }
    }
}
