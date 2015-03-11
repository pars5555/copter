/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import de.taimos.gpsd4java.api.ObjectListener;
import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;
import de.taimos.gpsd4java.types.ATTObject;
import de.taimos.gpsd4java.types.DeviceObject;
import de.taimos.gpsd4java.types.DevicesObject;
import de.taimos.gpsd4java.types.SATObject;
import de.taimos.gpsd4java.types.SKYObject;
import de.taimos.gpsd4java.types.TPVObject;
import de.taimos.gpsd4java.types.subframes.SUBFRAMEObject;
import java.io.IOException;
import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

/**
 *
 * @author Pars
 */
public class GpsdConnector implements Runnable {

    private static GpsdConnector instance = null;
    private Thread thread = null;
    private static Logger logger = null;
    private GPSdEndpoint ep = null;
    private TPVObject tpvObject = null;
    private WebSocket conn;
    private boolean streamGpsData = false;
    private Long tpvSetTimeMillis = null;
    private boolean connected = false;

    private GpsdConnector() {
        logger = Logger.getInstance();
    }

    public static GpsdConnector getInstance() {
        if (instance == null) {
            instance = new GpsdConnector();
        }
        return instance;
    }

    private void initGpsd() {
        try {
            Runtime.getRuntime().exec(Config.getInstance().getString("gps", "gpsd_kill"));
            Runtime.getRuntime().exec(Config.getInstance().getString("gps", "gpsd_set_device"));
            Thread.sleep(2000);
        } catch (Exception ex) {
            logger.log(ex.getMessage());
        }
    }

    public TPVObject getTpvObject() {
        return tpvObject;
    }

    public void setTpvObject(TPVObject tpvObject) {
        if (!Double.isNaN(tpvObject.getLongitude()) && !Double.isNaN(tpvObject.getLatitude())) {
            tpvSetTimeMillis = System.currentTimeMillis();
            this.tpvObject = tpvObject;
        }
    }

    private void initMembers() {
        thread = null;
        ep = null;
        tpvObject = null;
        tpvSetTimeMillis = System.currentTimeMillis();
        connected = false;
    }

    public void init() {
        logger.log("Init GPS started...");
        if (connected) {
            logger.log("GPS is already connected, no try to connect again.");
            return;
        }
        initMembers();
        initGpsd();
        String gpsd_host = Config.getInstance().getString("gps", "gpsd_host");
        int gpsd_port = Config.getInstance().getInt("gps", "gpsd_port");
        ep = null;
        try {
            ep = new GPSdEndpoint(gpsd_host, gpsd_port, new ResultParser());
        } catch (IOException ex) {
            logger.log(ex.getMessage());
        }
        if (ep != null) {
            ep.addListener(new ObjectListener() {

                @Override
                public void handleTPV(final TPVObject tpv) {
                    setTpvObject(tpv);
                    if (streamGpsData) {
                        JSONObject res = new JSONObject();
                        res.put("lng", tpv.getLongitude());
                        res.put("lat", tpv.getLatitude());
                        conn.send(res.toJSONString());
                    }
                }

                @Override
                public void handleSKY(final SKYObject sky) {
                    for (final SATObject sat : sky.getSatellites()) {
                    }
                }

                @Override
                public void handleSUBFRAME(final SUBFRAMEObject subframe) {
                }

                @Override
                public void handleATT(final ATTObject att) {
                }

                @Override
                public void handleDevice(final DeviceObject device) {
                }

                @Override
                public void handleDevices(final DevicesObject devices) {
                    for (final DeviceObject d : devices.getDevices()) {
                    }
                }
            });
            ep.start();
        }
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(this);
        thread.start();
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        connected = true;
        while (true) {
            try {
                Thread.sleep(200);
                if (ep == null || System.currentTimeMillis() - tpvSetTimeMillis > 1000 * 10 /*10 seconds*/) {
                    this.connected = false;
                    this.init();
                    break;
                }
                ep.watch(true, true);
            } catch (Exception ex) {
                logger.log(ex.getMessage());
            }
        }
    }

    public String doAction(JSONObject jsonParam, WebSocket conn) {
        this.conn = conn;
        if (!jsonParam.containsKey("action")) {
            return "Missing 'action' param!";
        }
        String action = (String) jsonParam.get("action");
        switch (action) {
            case Constants.START_STREAM_GPS_DATA:
                streamGpsData = true;
                return "Gps data streaming started...";
            case Constants.STOP_STREAM_GPS_DATA:
                streamGpsData = false;
                return "Gps data streaming stoped";
        }
        return "Unknown 'action' param: " + action;
    }
}
