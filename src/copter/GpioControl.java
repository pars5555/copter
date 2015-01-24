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
import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * @author default
 */
public class GpioControl {

    private static GpioControl instance = null;
    private static Logger logger = null;
    private final GpioController gpio;
    private List<GpioPinDigitalOutput> pins;

    private GpioControl() {
        logger = Logger.getInstance();
        gpio = GpioFactory.getInstance();
    }

    public void init() {
        this.initAllPinsAsOutput();
    }

    private void initAllPinsAsOutput() {
        pins = new ArrayList<>();
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_17));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_18));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_19));
        pins.add(gpio.provisionDigitalOutputPin(RaspiPin.GPIO_20));
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
            pins.get(pinNumber).high();
        } else {
            pins.get(pinNumber).low();
        }
    }

    /**
     *
     * @param pinNumber 0 based
     */
    public void togglePinState(Integer pinNumber) {
        pins.get(pinNumber).toggle();
    }

    /**
     *
     * @param pinNumber 0 based
     * @param milliseconds duration
     * @param state true->hight, false->low
     */
    public void pulsePin(Integer pinNumber, Integer milliseconds, boolean state) {
        pins.get(pinNumber).pulse(milliseconds, state);
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
                pinState = (int) (long) jsonParam.get("pin_state");
                this.pulsePin(pinNumber, durationMilliseconds, pinState != 0);
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
