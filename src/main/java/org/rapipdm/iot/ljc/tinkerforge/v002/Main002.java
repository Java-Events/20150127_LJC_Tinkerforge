package org.rapipdm.iot.ljc.tinkerforge.v002;

import com.tinkerforge.*;
import org.rapipdm.iot.ljc.tinkerforge.Localhost;

import java.io.IOException;

import static java.lang.System.out;

/**
 * Created by sven on 28.01.15.
 */
public class Main002 {
    private static final String UID_AMBIENT_LIGHT = "mbL"; // Change to your UID
    private static final String UID_LCD20X4 = "qzP"; // Change to your UID


    public static void main(String[] args)
            throws AlreadyConnectedException, IOException, TimeoutException, NotConnectedException {

        final IPConnection ipcon = new IPConnection();
        final LCD20x4 lcd20x4 = new LCD20x4(UID_LCD20X4, ipcon);
        BrickletAmbientLight al = new BrickletAmbientLight(UID_AMBIENT_LIGHT, ipcon);

        ipcon.connect(Localhost.HOST, Localhost.PORT);
        lcd20x4.init();
        al.setIlluminanceCallbackPeriod(500);

        al.addIlluminanceListener(illuminance -> {
            double v = illuminance / 10.0;
            String message = "Illuminance: " + v + " Lux";
            out.println(message);
            //print to LCD
            lcd20x4.printLine(1,message );
        });

        out.println("Press key to exit");
        System.in.read();
        ipcon.disconnect();


    }
}
