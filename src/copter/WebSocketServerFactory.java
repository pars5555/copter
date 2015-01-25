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
public class WebSocketServerFactory extends WebSocketServer {

    private final Config conf;
    private final Logger logger;

    public WebSocketServerFactory(int port) {
        super(new InetSocketAddress(port));
        this.conf = Config.getInstance();
        this.logger = Logger.getInstance();
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

        logger.log("websocket message received: " + message);

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
            switch (command) {
                case Constants.CAMERA_COMMAND:
                    res.put("message", CameraControl.getInstance().doAction(jsonObj) );
                case Constants.GPIO_COMMAND:
                    res.put("message", GpioControl.getInstance().doAction(jsonObj));
                    break;
            }
        } catch (Exception ex) {

            logger.log("Json parse error: : " + ex.getMessage());

        }
        if (!res.isEmpty()) {
            conn.send(res.toJSONString());
            logger.log("websocket response: " + res.toJSONString());

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
