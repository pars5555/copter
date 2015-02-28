/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

/**
 *
 * @author Pars
 */
public class Engine {

    private static Engine instance = null;
    private int throttle;
    private int yaw;
    private int pitch;
    private int roll;
    private WebSocket conn;

    private Engine() {

    }

    public static Engine getInstance() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }

    private void StartEngine() {
    }

    private float getThrottle() {
        return throttle;
    }

    private void setThrottle(int throttle) {
        this.throttle = throttle;
    }

    private float getYaw() {
        return yaw;
    }

    private void setYaw(int yaw) {
        this.yaw = yaw;
    }

    private float getPitch() {
        return pitch;
    }

    private void setPitch(int pitch) {
        this.pitch = pitch;
    }

    private float getRoll() {
        return roll;
    }

    private void setRoll(int roll) {
        this.roll = roll;
    }

    public String doAction(JSONObject jsonParam, WebSocket conn) {
        this.conn = conn;
        if (!jsonParam.containsKey("action")) {
            return "Missing 'action' param!";
        }
        String action = (String) jsonParam.get("action");
        switch (action) {
            case Constants.SET_PITCH:
                int p = (int) (long) jsonParam.get("value");
                this.setPitch(p);
                return "Pitch set to " + p;
            case Constants.SET_ROLL:
                int r = (int) (long) jsonParam.get("value");
                this.setPitch(r);
                return "Roll set to " + r;
            case Constants.SET_THROTTLE:
                int th = (int) (long) jsonParam.get("value");
                this.setPitch(th);
                return "Throttle set to " + th;
            case Constants.SET_YAW:
                int y = (int) (long) jsonParam.get("value");
                this.setPitch(y);
                return "Yaw set to " + y;
        }
        return "Unknown 'action' param: " + action;
    }

}
