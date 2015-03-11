/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import copter.dto.CopterGpsData;
import de.taimos.gpsd4java.types.TPVObject;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author default
 */
public class GpsDataPoster implements Runnable {

    private static GpsDataPoster instance = null;
    private static Logger logger = null;
    private Thread thread = null;

    private GpsDataPoster() {
        logger = Logger.getInstance();
    }

    void init() {
        GpsdConnector.getInstance().init();
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(this);
        thread.start();
    }

    public static GpsDataPoster getInstance() {
        if (instance == null) {
            instance = new GpsDataPoster();
        }
        return instance;
    }

    public CopterGpsData getCopterGpsData() {
        TPVObject tpvObject = GpsdConnector.getInstance().getTpvObject();
        if (tpvObject != null) {
            return new CopterGpsData(tpvObject.getLongitude(), tpvObject.getLatitude(), tpvObject.getAltitude(), tpvObject.getSpeed(), tpvObject.getClimbRate());
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (InternetConnector.getInstance().checkConnectionToServer()) {
                    if (GpsdConnector.getInstance().isConnected()) {
                        CopterGpsData copterGpsData = getCopterGpsData();
                        if (copterGpsData != null) {
                            String jsonResponse = ServerConnection.getInstance().sendCopterGeoInfoToServer(copterGpsData);
                            JSONParser parser = new JSONParser();
                            Object obj = parser.parse(jsonResponse);
                            JSONObject jsonObj = (JSONObject) obj;
                            String status = (String) jsonObj.get("status");
                            if (status == null || !status.equals("ok")) {
                                logger.log("Gps data sending server response is not OK! response: "+jsonResponse);
                            }
                        } else {
                            logger.log("Gps data in null! data is not sent to server.");
                        }
                    } else {
                        logger.log("Gps is off! data is not sent to server.");
                    }
                }
                Thread.sleep(Config.getInstance().getInt("gps", "date_send_to_server_interval_seconds") * 1000);
            } catch (Exception ex) {
                logger.log(ex.getMessage());
            }
        }
    }

}
