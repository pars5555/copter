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
public class Engine {

    private static Engine instance = null;

    private Engine() {

    }

    public static Engine getInstance() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }
    
    public void StartEngine()
    {
    }
    
    

}
