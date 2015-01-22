/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

/**
 *
 * @author default
 */
public class GpioControl {

    private static GpioControl instance = null;
    private static Logger logger = null;
    private final GpioController gpio;

    private GpioControl() {
        logger = Logger.getInstance();
        gpio = GpioFactory.getInstance();
    }

    public static GpioControl getInstance() {
        if (instance == null) {
            instance = new GpioControl();
        }
        return instance;
    }

}
