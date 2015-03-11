/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

public class HCSR04 implements Runnable {

    private static HCSR04 instance = null;
    private Logger logger = null;
    private Thread thread = null;
    private GpioPinDigitalOutput trigger;
    private GpioPinDigitalInput echo;
    private WebSocket conn;
    private boolean streamData = false;
    private static final float soundSpeed = 340.3f * 100;

    private HCSR04() {
        logger = Logger.getInstance();
    }

    public static HCSR04 getInstance() {
        if (instance == null) {
            instance = new HCSR04();
        }
        return instance;
    }

    public void init() {
        final GpioController gpio = GpioFactory.getInstance();
        trigger = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "Trig", PinState.LOW);
        echo = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, "Echo");
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(this);
        thread.start();
    }

    private void trigger(GpioPinDigitalOutput pin) {
        pin.setState(true);
        delay(0, 20000);
        pin.setState(false);
    }

    private boolean echoHigh(GpioPinDigitalInput pin) {
        for (int i = 0; i < 5000; i++) {
            if (pin.getState().isHigh()) {
                return true;
            }
        }
        return false;
    }

    private boolean echoLow(GpioPinDigitalInput pin) {
        for (int i = 0; i < 5000; i++) {
            if (pin.getState().isLow()) {
                return true;
            }
        }
        return false;
    }

    private float getDistance(GpioPinDigitalOutput pinTrig, GpioPinDigitalInput pinEcho) {
        float result = 0.0F;
        trigger(pinTrig);
        if (!echoHigh(pinEcho)) {
            return -1;
        }
        long start = System.nanoTime();
        if (!echoLow(pinEcho)) {
            return -1;
        }
        long end = System.nanoTime();
        long pulse = end - start;
        result = pulse / 1_000_000_000F;
        result = result * soundSpeed / 2;
        return result;
    }

    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }
    }

    private void delay(int ms, int ns) {
        try {
            Thread.sleep(ms, ns);
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void run() {
        delay(2000);
        while (streamData) {
            float distance = getDistance(trigger, echo);
            JSONObject res = new JSONObject();
            res.put("fron_distance_cm", distance);
            conn.send(res.toJSONString());
            delay(100);
        }
    }

    public String doAction(JSONObject jsonParam, WebSocket conn) {
        this.conn = conn;
        if (!jsonParam.containsKey("action")) {
            return "Missing 'action' param!";
        }
        String action = (String) jsonParam.get("action");
        switch (action) {
            case Constants.START_STREAM_DISTANCE_DATA:
                streamData = true;
                init();
                return "Distance data streaming started...";
            case Constants.STOP_STREAM_DISTANCE_DATA:
                streamData = false;
                if (thread != null) {
                    thread.interrupt();
                }
                return "Distance data streaming stoped";
        }
        return "Unknown 'action' param: " + action;
    }
}
