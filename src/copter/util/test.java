/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter.util;

/**
 *
 * @author default
 */
public class test {

    public static void main(String[] args) throws InterruptedException {
        AdafruitPCA9685 servoBoard = new AdafruitPCA9685();   // 0x40 is the default address
        servoBoard.setPWMFreq(60); // Set frequency to 60 Hz
        int servoMin = 150;   // Min pulse length out of 4096
        int servoMax = 600;   // Max pulse length out of 4096

        final int CONTINUOUS_SERVO_CHANNEL = 14;
        final int STANDARD_SERVO_CHANNEL = 15;

        for (int i = 0; i < 10; i++) {
            System.out.println("i=" + i);
            servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, servoMin);
            servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, servoMin);
            Thread.sleep(1000);
            servoBoard.setPWM(STANDARD_SERVO_CHANNEL, 0, servoMax);
            servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, servoMax);
            Thread.sleep(1000);
        }
        servoBoard.setPWM(CONTINUOUS_SERVO_CHANNEL, 0, 0); // Stop the continuous one
        System.out.println("Done with the demo.");
    }
}
