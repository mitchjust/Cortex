/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bluescopesteel.cortex;

import com.bluescopesteel.cortex.interfaces.console.ConsoleInterface;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mitch
 */
public class TestMain {

    public int myInt = 6;
    
    public InnerClass inner = new InnerClass();
    
    public Object object;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Cortex cortex = Cortex.getInstance();
        cortex.registerInterface(new ConsoleInterface());

        TestMain test = new TestMain();
        
        cortex.addVariable("test", test);
        
        
        cortex.addVariable("args", args);
        
    }
    
    public String getString(){
        return "String@@#@$";
        
    }
    
    public void longOperation() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public class InnerClass {
        public String testString = "testst";
        public String getString() { 
            return "Method!";
        }
    }
    
}
