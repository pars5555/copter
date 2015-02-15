/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

/**
 *
 * @author Pars
 */
abstract public class Constants {

    public static final String PING_COMMAND = "ping";
    public static final String GPIO_COMMAND = "gpio";
    public static final String GPIO_PULSE_ACTION = "pulse";
    public static final String GPIO_SET_PIN_STATE_ACTION = "set_pin_state";
    public static final String GPIO_TOGGLE_PIN_STATE_ACTION = "toggle_pin_state";
    public static final String CAMERA_COMMAND = "camera";
    public static final String MPU_COMMAND = "mpu";
    public static final String CAMERA_START_HTTP_STREMING_ACTION = "http_start_streaming";
    public static final String CAMERA_START_RTMP_STREAMING_ACTION = "rtmp_start_streaming";
    public static final String SET_ACCELEROMETER_ON_ACTION = "set_accelerometer_on";
    public static final String SET_ACCELEROMETER_OFF_ACTION = "set_accelerometer_off";
    public static final String SET_GYO_ON_ACTION = "set_gyro_on";
    public static final String SET_GYO_OFF_ACTION = "set_gyro_off";
    public static final String CAMERA_STOP_STREAMING_COMMAND = "stop_streaming";
}
