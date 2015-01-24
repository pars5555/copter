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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    /**
     *
     * @param pinNumber 0 based
     * @param state
     */
    public void setPinState(Integer pinNumber, boolean state) {
        if (state) {
            this.getOutputPin(pinNumber).high();
        } else {
            this.getOutputPin(pinNumber).low();
        }
    }

    /**
     *
     * @param pinNumber 0 based
     */
    public void togglePinState(Integer pinNumber) {
        this.getOutputPin(pinNumber).toggle();
    }

    /**
     *
     * @param pinNumber 0 based
     * @param milliseconds duration
     * @param state true->hight, false->low
     */
    public void pulsePin(Integer pinNumber, Integer milliseconds) {
        this.getOutputPin(pinNumber).pulse(milliseconds);
    }

    public void doAction(JSONObject jsonParam) {
        if (!jsonParam.containsKey("action")) {
            return;
        }
        String action = (String) jsonParam.get("action");
        int pinNumber;
        int pinState;
        switch (action) {
            case "pulse":
                pinNumber = (int) (long) jsonParam.get("pin_number");
                int durationMilliseconds = (int) (long) jsonParam.get("duration_milliseconds");
                this.pulsePin(pinNumber, durationMilliseconds);
                break;
            case "set_pin_state":
                pinNumber = (int) (long) jsonParam.get("pin_number");
                pinState = (int) (long) jsonParam.get("pin_state");
                this.setPinState(pinNumber, pinState != 0);
                break;
            case "toggle_pin_state":
                pinNumber = (int) (long) jsonParam.get("pin_number");
                this.togglePinState(pinNumber);
                break;
        }
    }

}
