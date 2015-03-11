/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

public final class MPU9150 implements Runnable {

    private static MPU9150 instance;
    private I2CBus bus;
    private I2CDevice device;
    private byte[] accelData, gyroData;
    private boolean sendAccelData;
    private boolean sendGyroData;
    private Thread thread = null;
    private WebSocket conn;

    public static MPU9150 getInstance() {
        if (instance == null) {
            instance = new MPU9150();
        }
        return instance;
    }

    private MPU9150() {
        sendAccelData = false;
        sendGyroData = false;
        thread = new Thread(this);
        thread.start();
        try {
            //get i2c bus
            bus = I2CFactory.getInstance(I2CBus.BUS_1);
            //System.out.println("Connected to bus OK!");

            //get device itself
            device = bus.getDevice(0x68);
            //System.out.println("Connected to device OK!");

            //start sensing, using config registries 6B  and 6C    
            device.write(0x6B, (byte) 0b00000000);
            device.write(0x6C, (byte) 0b00000000);
            //System.out.println("Configuring Device OK!");

            //config gyro
            device.write(0x1B, (byte) 0b11100000);
            //config accel    
            device.write(0x1C, (byte) 0b00000001);
            // System.out.println("Configuring sensors OK!");

        } catch (IOException e) {
            //System.out.println(e.getMessage());
        }
    }

    private List<Double> readAccel() {

        try {
            accelData = new byte[6];
            //gyroData = new byte[6];

            //You can read one registry at a time,
            //or you can read multiple consecutive ones, 
            //in our case we are reading 6 consecutive registries
            //from 0x3B, meaning we are reading all the 
            //accelerometer measurements
            int r = device.read(0x3B, accelData, 0, 6);
            if (r != 6) {
                //System.out.println("Error reading accel data, < 6 bytes");
            }
            //Convert the values to integers, using the
            //helper method asInt
            int accelX = ((int) (accelData[0] / 4));
            int accelY = ((int) (accelData[2] / 4));
            int accelZ = ((int) (accelData[4] / 4));
            double Ax = Math.atan(accelX / Math.sqrt(accelY * accelY + accelZ * accelZ)) * 180 / Math.PI;
            double Ay = Math.atan(accelY / Math.sqrt(accelX * accelX + accelZ * accelZ)) * 180 / Math.PI;
            double Az = Math.atan(accelZ / Math.sqrt(accelX * accelX + accelY * accelY)) * 180 / Math.PI;
            List<Double> ret = new ArrayList<>();
            Ax = Math.round(Ax);
            Ay = Math.round(Ay);
            Az = Math.round(Az);
            ret.add(Ax);
            ret.add(Ay);
            ret.add(Az);
            return ret;
        } catch (IOException ex) {
            Logger.getLogger(MPU9150.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    private List<Double> readGyro() {

        try {
            gyroData = new byte[6];
            int r = device.read(0x43, gyroData, 0, 6);
            if (r != 6) {
                //System.out.println("Error reading gyro data, < 6 bytes");
            }
            List<Double> ret = new ArrayList<>();
            ret.add(0d);
            ret.add(0d);
            ret.add(0d);
            //Convert the values to integers, using the
            //helper method asInt
            //int gyroX = (asInt(gyroData[0]) * 256 + asInt(gyroData[1]));
            //int gyroY = (asInt(gyroData[2]) * 256 + asInt(gyroData[3]));
            //int gyroZ = (asInt(gyroData[4]) * 256 + asInt(gyroData[5]));
            // System.err.println(gyroData[0]);
            //ret.add(gyroX);
            //ret.add(gyroY);
            //ret.add(gyroZ);
            return ret;
        } catch (IOException ex) {
            Logger.getLogger(MPU9150.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String doAction(JSONObject jsonParam, WebSocket conn) {
        this.conn = conn;
        if (!jsonParam.containsKey("action")) {
            return "Missing 'action' param!";
        }
        String action = (String) jsonParam.get("action");
        switch (action) {
            case Constants.SET_ACCELEROMETER_ON_ACTION:
                sendAccelData = true;
                return "Accelerometer data sending on";
            case Constants.SET_ACCELEROMETER_OFF_ACTION:
                sendAccelData = false;
                return "Accelerometer data sending off";
            case Constants.SET_GYO_ON_ACTION:
                sendGyroData = true;
                return "Gyroscope data sending on";
            case Constants.SET_GYO_OFF_ACTION:
                sendGyroData = false;
                return "Gyroscope data sending off";
        }
        return "Unknown 'action' param: " + action;
    }

    @Override
    public void run() {
        JSONObject res = new JSONObject();
        while (true) {
            try {
                if (sendAccelData || sendGyroData) {
                    res.clear();
                }
                if (sendAccelData) {
                    List<Double> readAccel = this.readAccel();
                    res.put("accelX", readAccel.get(0));
                    res.put("accelY", readAccel.get(1));
                    res.put("accelZ", readAccel.get(2));
                }

                if (sendGyroData) {
                    List<Double> readGyro = this.readGyro();
                    res.put("gyroX", readGyro.get(0));
                    res.put("gyroY", readGyro.get(1));
                    res.put("gyroZ", readGyro.get(2));
                }
                if (sendAccelData || sendGyroData) {
                    this.conn.send(res.toJSONString());
                }
                Thread.sleep(50);

            } catch (Exception ex) {
                Logger.getLogger(MPU9150.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
