/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 *
 * @author default
 */
public class GpioControl {

    private static GpioControl instance = null;
    private static Logger logger = null;
    private final GpioController gpio;
    private final Map<Integer, GpioPinDigitalOutput> pins;

    private GpioControl() {
        logger = Logger.getInstance();
        gpio = GpioFactory.getInstance();
        pins = new HashMap<>();
    }

    private GpioPinDigitalOutput getOutputPin(Integer pinNumber) {
        if (pins.containsKey(pinNumber)) {
            return pins.get(pinNumber);
        }
        switch (pinNumber) {
            case 0:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00));
                break;
            case 1:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01));
                break;
            case 2:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02));
                break;
            case 3:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03));
                break;
            case 4:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04));
                break;
            case 5:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05));
                break;
            case 6:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06));
                break;
            case 7:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07));
                break;
            case 8:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08));
                break;
            case 9:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09));
                break;
            case 10:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10));
                break;
            case 11:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11));
                break;
            case 12:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12));
                break;
            case 13:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13));
                break;
            case 14:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14));
                break;
            case 15:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15));
                break;
            case 16:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16));
                break;
            case 17:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_17));
                break;
            case 18:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_18));
                break;
            case 19:
                pins.put(pinNumber, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_19));
                break;
        }
        return pins.get(pinNumber);
    }

    public static GpioControl getInstance() {
        if (instance == null) {
            instance = new GpioControl();
        }
        return instance;
    }

    public String setPinState(Integer pinNumber, boolean state) {
        if (state) {
            getOutputPin(pinNumber).high();
            return "Set GPIO pin #" + pinNumber.toString() + " state to HIGH";
        } else {
            getOutputPin(pinNumber).low();
            return "Set GPIO pin #" + pinNumber.toString() + " state to LOW";
        }
    }

    public String togglePinState(Integer pinNumber) {
        getOutputPin(pinNumber).toggle();
        return "Toggle GPIO pin #" + pinNumber.toString() + " state";
    }

    public String pulsePin(Integer pinNumber, Integer milliseconds) {
        getOutputPin(pinNumber).pulse(milliseconds);
        return "Pulse to GPIO pin #" + pinNumber.toString() + " duration: " + milliseconds.toString();
    }

    public String doAction(JSONObject jsonParam) {
        if (!jsonParam.containsKey("action")) {
            return "Missing 'action' param!";
        }
        String action = (String) jsonParam.get("action");
        int pinNumber;
        int pinState;
        switch (action) {
            case Constants.GPIO_PULSE_ACTION:
                pinNumber = (int) (long) jsonParam.get("pin_number");
                int durationMilliseconds = (int) (long) jsonParam.get("duration_milliseconds");
                return this.pulsePin(pinNumber, durationMilliseconds);
            case Constants.GPIO_SET_PIN_STATE_ACTION:
                pinNumber = (int) (long) jsonParam.get("pin_number");
                pinState = (int) (long) jsonParam.get("pin_state");
                return this.setPinState(pinNumber, pinState != 0);
            case Constants.GPIO_TOGGLE_PIN_STATE_ACTION:
                pinNumber = (int) (long) jsonParam.get("pin_number");
                return this.togglePinState(pinNumber);
        }
        return "Unknown 'action' param: " + action;
    }

}
