/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

    public static String getIpAddress() {
        String hostAddress = null;
        try {
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            for (; n.hasMoreElements();) {
                NetworkInterface e = n.nextElement();

                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements();) {
                    InetAddress addr = a.nextElement();
                    hostAddress = addr.getHostAddress();
                    break;
                }
                break;
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hostAddress;
    }

    /**
     * this function make a post request to server and get response it sends
     * copter IP Address, Unique ID and Name
     *
     * @param ipAddress
     */
    private static String sendCopterInformationToServer(String ipAddress) {

        String serverHost = Config.getInstance().getString("main", "server_host");
        String copter_unique_id = Config.getInstance().getString("copter", "unique_id");
        String copter_name = Config.getInstance().getString("copter", "name");
        URL url;
        try {
            url = new URL("http://" + serverHost + Config.getInstance().getString("main", "register_copter_url"));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            String urlParameters = "copter_ip=" + ipAddress + "&unique_id=" + copter_unique_id + "&name=" + copter_name;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            if (Config.getInstance().getInt("main", "dev_mode") == 1) {
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String jsonResponse = response.toString();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonResponse);
            JSONObject jsonObj = (JSONObject) obj;
            String status = (String) jsonObj.get("status");
            return status;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private static String obtainIpAddressAndSendCopterInfoToServer() {
        String ipAddress = getIpAddress();
        if (ipAddress != null && !ipAddress.isEmpty()) {
            return sendCopterInformationToServer(ipAddress);
        } else {
            return "No raspberry ip address!";
        }
    }

    public static void main(String[] args) {
        /*connecting to the internet*/
        //InternetConnector.getInstance().connectBySakis3g();
        Integer cycleNumber = 1;
        while (!InternetConnector.getInstance().checkConnectionToServer()) {
            try {
                Thread.sleep(3000);
                if (Config.getInstance().getInt("main", "dev_mode") == 1) {
                    System.out.println("Checking connection to the server..." + cycleNumber.toString());
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            cycleNumber++;
            if (cycleNumber >= Config.getInstance().getInt("main", "initial_internet_checking_cycle_count")) {
                if (Config.getInstance().getInt("main", "dev_mode") == 1) {
                    System.out.println("Error: could not connect to the internet.");
                }
                exitSystem();
            }
        }
        String serverResponse = obtainIpAddressAndSendCopterInfoToServer();
        if (serverResponse.equals("ok")) {
            int port = Config.getInstance().getInt("main", "control_port");
            WebSocketServerFactory wf = new WebSocketServerFactory(port);
            wf.start();
        } else {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, serverResponse);
            exitSystem();
        }

    }

    private static void exitSystem() {
        if (Config.getInstance().getInt("main", "dev_mode") == 1) {
            System.out.println("Exitting the program... bye.");
        }
        System.exit(1);
    }

}
