/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import copter.dto.CopterGpsData;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author default
 */
public class ServerConnection implements Runnable {

    private static ServerConnection instance = null;
    private Thread thread = null;
    private Process process = null;
    private static Logger logger = null;
    private String ip = "";

    private ServerConnection() {
        logger = Logger.getInstance();
    }

    public static ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }

    public void init() {
        while (true) {
            InternetConnector.getInstance().connectBySakis3g();
            if (this.checkServerConnection()) {
                String serverResponse = obtainIpAddressAndSendCopterInfoToServer();
                if (serverResponse != null && serverResponse.equals("ok")) {
                    GpsDataPoster.getInstance().init();
                    int port = Config.getInstance().getInt("main", "control_port");
                    WebSocketServerFactory wf = new WebSocketServerFactory(port);
                    wf.start();
                    break;
                } else {
                    InternetConnector.getInstance().disconnectFromSakis3g();
                    logger.log(serverResponse);
                }
            }
        }
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(this);
        thread.start();
    }

    private boolean checkServerConnection() {
        Integer cycleNumber = 1;
        while (true) {
            //!
            if (InternetConnector.getInstance().checkConnectionToServer()) {
                return true;
            }
            try {
                Thread.sleep(3000);

                logger.log("Checking connection to the server..." + cycleNumber.toString());

            } catch (InterruptedException ex) {
                logger.log(ex.getMessage());
                return false;
            }
            cycleNumber++;
            if (cycleNumber >= Config.getInstance().getInt("main", "initial_internet_checking_cycle_count")) {

                logger.log("Error: could not connect to the internet. Trying to reconnect...");
                return false;

            }
        }
    }

    private String getIpAddress() {
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
            logger.log(ex.getMessage());
        }
        return hostAddress;
    }

    /**
     * this function make a post request to server and send the copter geo info
     */
    public String sendCopterGeoInfoToServer(CopterGpsData gpsData) {
        if (gpsData == null) {
            return "Empty gps data!";
        }
        String copter_unique_id = Config.getInstance().getString("copter", "unique_id");
        JSONObject obj = new JSONObject();
        obj.putAll(gpsData.toMap());
        String postUrl = Config.getInstance().getString("main", "send_gps_data_server_url");
        Map parameters = new HashMap<String, String>();
        parameters.put("unique_id", copter_unique_id);
        parameters.put("gps_data_json", obj.toJSONString());
        return postToServer(postUrl, parameters);
    }

    private String postToServer(String postUrl, Map<String, String> parameters) {
        String serverHost = Config.getInstance().getString("main", "server_host");
        URL url;
        try {
            url = new URL("http://" + serverHost + postUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            String urlParameters = "";
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                String encodedParamValue = URLEncoder.encode(paramValue, "UTF-8");
                urlParameters += paramName + "=" + encodedParamValue + "&";
            }
            if (urlParameters.endsWith("&")) {
                urlParameters = urlParameters.substring(0, urlParameters.length() - 1);
            }
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            //logger.log("\nSending 'POST' request to URL : " + url);
            //logger.log("Post parameters : " + urlParameters);
            //logger.log("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String serverResponse = response.toString();
            return serverResponse;
        } catch (Exception ex) {
            logger.log(ex.getMessage());
            return null;
        }
    }

    /**
     * this function make a post request to server and get response it sends
     * copter IP Address, Unique ID and Name
     *
     * @param ipAddress
     */
    private String registerCopterOnServer(String ipAddress) {

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

            logger.log("\nSending 'POST' request to URL : " + url);
            logger.log("Post parameters : " + urlParameters);
            logger.log("Response Code : " + responseCode);

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
            if (status != null && status.equals("ok")) {
                return "ok";
            } else {
                return (String) jsonObj.get("message");
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String obtainIpAddressAndSendCopterInfoToServer() {
        String ipAddress = getIpAddress();
        if (ipAddress != null && !ipAddress.isEmpty()) {
            this.ip = ipAddress;
            return registerCopterOnServer(ipAddress);
        } else {
            logger.log("No raspberry ip address!");
            return "No raspberry ip address!";
        }
    }

    @Override
    public void run() {
        String ipAddress = null;
        String serverResponse = null;
        while (true) {
            if (!InternetConnector.getInstance().checkInternetConnection()) {
                logger.log("internet is down! reconnecting...");
                break;
            }
            if (!InternetConnector.getInstance().checkConnectionToServer()) {
                logger.log("Internet is up, but server is down!");
            }
            ipAddress = getIpAddress();
            if (ipAddress == null || ipAddress.isEmpty()) {
                logger.log("can not obtain device IP address!!! start connection again...");
                break;
            }
            if (this.ip == null || !this.ip.equals(ipAddress)) {
                logger.log("device IP is changed!!! sending IP to server again...");
                this.ip = ipAddress;
                serverResponse = obtainIpAddressAndSendCopterInfoToServer();
                if (serverResponse == null || !serverResponse.equals("ok")) {
                    logger.log(serverResponse);
                    break;
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                logger.log(ex.getMessage());
            }

        }
        InternetConnector.getInstance().disconnectFromSakis3g();
        init();
    }
}
