/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copter;

public class Main {

    private static Logger logger = null;
    private static Main instance = null;

    private Main() {
        logger = Logger.getInstance();
        logger.log("started");
        //BlutoothServer.getInstance().start();
        /*connecting to the internet*/
        ServerConnection.getInstance().init();
        GpsDataPoster.getInstance().init();
    }

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    public static void main(String[] args) {
        Main.getInstance();
    }

    public void exitSystem() {
        if (Config.getInstance().getInt("main", "dev_mode") == 1) {
            logger.log("Exitting the program... bye.");
        }
        System.exit(1);
    }

}
