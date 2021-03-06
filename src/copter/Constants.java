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
    public static final String REBOOT_COMMAND = "reboot";
    public static final String CAMERA_START_HTTP_STREMING_ACTION = "http_start_streaming";
    public static final String CAMERA_START_RTMP_STREAMING_ACTION = "rtmp_start_streaming";
    public static final String MPU_COMMAND = "mpu";
    public static final String SET_ACCELEROMETER_ON_ACTION = "set_accelerometer_on";
    public static final String SET_ACCELEROMETER_OFF_ACTION = "set_accelerometer_off";
    public static final String SET_GYO_ON_ACTION = "set_gyro_on";
    public static final String SET_GYO_OFF_ACTION = "set_gyro_off";
    public static final String ENGINE_COMMAND = "engine";
    public static final String SET_THROTTLE_ACTION = "set_throttle";
    public static final String SET_YAW_ACTION = "set_yaw";
    public static final String SET_ROLL_ACTION = "set_roll";
    public static final String SET_PITCH_ACTION = "set_pitch";
    public static final String START_ENGINE_ACTION = "start_engine";
    public static final String CAMERA_STOP_STREAMING_COMMAND = "stop_streaming";
    public static final String GPS_COMMAND = "gps";
    public static final String START_STREAM_GPS_DATA = "start_gps_streaming";
    public static final String STOP_STREAM_GPS_DATA = "stop_gps_streaming";
    public static final String HCSR04_COMMAND = "hcsr04";
    public static final String START_STREAM_DISTANCE_DATA = "start_distance_streaming";
    public static final String STOP_STREAM_DISTANCE_DATA = "stop_distance_streaming";
}
