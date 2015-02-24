/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Pars
 */
public class WebSocketServerFactory extends WebSocketServer implements Runnable {

    private final Config conf;
    private final Logger logger;
    private final MPU9150 mpu9150;

    public WebSocketServerFactory(int port) {
        super(new InetSocketAddress(port));
        this.conf = Config.getInstance();
        this.logger = Logger.getInstance();
        this.mpu9150 = MPU9150.getInstance();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        if (conf.getInt("main", "dev_mode") == 1) {
            logger.log("websocket started...");
        }
        //Handle new connection here
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        JSONObject res = new JSONObject();
        String command = null;
        JSONObject jsonObj = null;
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(message);
            jsonObj = (JSONObject) obj;
            command = (String) jsonObj.get("command");

            if (command == null || command.isEmpty()) {
                return;
            }
            if (!command.equals(Constants.PING_COMMAND)) {
                logger.log("websocket message received: " + message);
            }
            switch (command) {
                case Constants.CAMERA_COMMAND:
                    res.put("message", CameraControl.getInstance().doAction(jsonObj));
                    break;
                case Constants.MPU_COMMAND:
                    res.put("message", mpu9150.doAction(jsonObj, conn));
                    break;
                case Constants.GPIO_COMMAND:
                    res.put("message", GpioControl.getInstance().doAction(jsonObj));
                    break;
                case Constants.PING_COMMAND:
                    res.put("status", "ok");
                    String ping_id = (String) jsonObj.get("ping_id");
                    res.put("ping_id", ping_id);
                    break;
            }

        } catch (Exception ex) {
            String err = "Json parse error: " + ex.getMessage();
            logger.log(err);
            res.put("message", err);
        }
        if (!res.isEmpty()) {
            conn.send(res.toJSONString());
            if (!command.equals(Constants.PING_COMMAND)) {
                logger.log("websocket response: " + res.toJSONString());
            }
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        logger.log("websocket closed");

    }

    @Override
    public void onError(WebSocket conn, Exception exc) {
        logger.log("websocket error: " + exc.getMessage());

    }

}
