
package com.oe.stagetablemaker;

import java.util.logging.Level;
import java.util.logging.Logger;


public class App {
    
    public static void main(String[] args) {


        try {

            String pathToFile = args[0];
            String chillersStr = args[1];

            //String pathToFile = "/Users/hal/Desktop/Art-1-8.csv";
            //String chillersStr = "1,2,3,4,5,6,7,8";

            ChillerStagingOrder cso = new ChillerStagingOrder( pathToFile, chillersStr );


            cso.DumpTable();
            cso.writeCSVtoDisk(pathToFile);


        } catch (Exception ex) {
            System.out.println("usage:");
            System.out.println("java -jar target/stagetablemaker-1.0-SNAPSHOT.jar pathToTable \"1,2,3,4,5,6\"");
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Bad command line arguments", ex);
        }

    }
    
}
