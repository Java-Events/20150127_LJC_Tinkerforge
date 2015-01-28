package org.rapipdm.iot.ljc.tinkerforge.v001;

import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.IPConnection;

/**
 * Created by sven on 27.01.15.
 *
 * Will write the Data on CommandLine
 *
 */
public class Main001 {
    private static final String HOST = "localhost";
    private static final int PORT = 4223;
    private static final String UID = "mbL"; // Change to your UID

    // Note: To make the example code cleaner we do not handle exceptions. Exceptions you
    //       might normally want to catch are described in the documentation
    public static void main(String args[]) throws Exception {
        IPConnection ipcon = new IPConnection(); // Create IP connection
        BrickletAmbientLight al = new BrickletAmbientLight(UID, ipcon); // Create device object

        ipcon.connect(HOST, PORT); // Connect to brickd
        // Don't use device before ipcon is connected

        // Set Period for illuminance callback to 1s (1000ms)
        // Note: The illuminance callback is only called every second if the
        //       illuminance has changed since the last call!
        al.setIlluminanceCallbackPeriod(1000);

        // Add and implement illuminance listener (called if illuminance changes)
        al.addIlluminanceListener(new BrickletAmbientLight.IlluminanceListener() {
            public void illuminance(int illuminance) {
                System.out.println("Illuminance: " + illuminance / 10.0 + " Lux");
            }
        });

        System.out.println("Press key to exit");
        System.in.read();
        ipcon.disconnect();
    }
}