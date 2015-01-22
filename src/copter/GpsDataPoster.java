/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import copter.dto.CopterGpsData;
import java.util.logging.Level;

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
        return new CopterGpsData(1, 2, 3, 4, 5);
    }

    @Override
    public void run() {
        while (true) {
            if (InternetConnector.getInstance().checkConnectionToServer()) {
                CopterGpsData copterGpsData = getCopterGpsData();
                logger.log(ServerConnection.getInstance().sendCopterGeoInfoToServer(copterGpsData));
            }
            try {
                Thread.sleep(Long.parseLong(Config.getInstance().getString("gps", "date_send_to_server_interval_seconds")));
            } catch (InterruptedException ex) {
                logger.log(ex.getMessage());
            }
        }
    }

}
