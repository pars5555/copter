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

    public WebSocketServerFactory(int port) {
        super(new InetSocketAddress(port));
        this.conf = Config.getInstance();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        if (Config.getInstance().getInt("main", "dev_mode") == 1) {
            System.err.println("websocket started...");
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
        } catch (Exception ex) {
            if (conf.getInt("main", "dev_mode") == 1) {
                System.err.println("Json parse error: : " + ex.getMessage());
            }
        }
        if (command == null || command.isEmpty()) {
            return;
        }
        switch (command) {
            case Constants.CAMERA_START_STREAMING:
                Integer width = (Integer) jsonObj.get("width");
                Integer height = (Integer) jsonObj.get("height");
                Integer fps = (Integer) jsonObj.get("fps");
                res.put("message", CameraControl.getInstance().startStreaming(width, height, fps));
                break;
            case Constants.CAMERA_STOP_STREAMING:
                res.put("message", CameraControl.getInstance().stopStreaming());
                break;
        }

        if (!res.isEmpty()) {
            conn.send(res.toJSONString());
            if (Config.getInstance().getInt("main", "dev_mode") == 1) {
                System.err.println("websocket response: " + res.toJSONString());
            }
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if (Config.getInstance().getInt("main", "dev_mode") == 1) {
            System.err.println("websocket closed");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception exc) {
        if (Config.getInstance().getInt("main", "dev_mode") == 1) {
            System.err.println("websocket error: " + exc.getMessage());
        }
    }

}
