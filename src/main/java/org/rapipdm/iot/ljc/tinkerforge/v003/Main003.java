package org.rapipdm.iot.ljc.tinkerforge.v003;

import com.tinkerforge.*;
import org.rapipdm.iot.ljc.tinkerforge.Localhost;
import org.rapipdm.iot.ljc.tinkerforge.v002.LCD20x4;

import java.io.IOException;

import static java.lang.System.out;

/**
 * Created by sven on 28.01.15.
 */
public class Main003 {

    private static final String UID_MULTITOUCH = "pdL";
    private static final String UID_AMBIENT_LIGHT = "mbL"; // Change to your UID
    private static final String UID_LCD20X4 = "qzP"; // Change to your UID

    public static void main(String[] args) throws IOException, NotConnectedException, AlreadyConnectedException, TimeoutException {

        final IPConnection ipcon = new IPConnection();

        final LCD20x4 lcd20x4 = new LCD20x4(UID_LCD20X4, ipcon);

        BrickletMultiTouch multiTouch = new BrickletMultiTouch(UID_MULTITOUCH, ipcon);
        BrickletAmbientLight ambientLight = new BrickletAmbientLight(UID_AMBIENT_LIGHT, ipcon);

        ipcon.connect(Localhost.HOST, Localhost.PORT);

        lcd20x4.init();
        ambientLight.setIlluminanceCallbackPeriod(500);
        ambientLight.addIlluminanceListener(illuminance -> {
            double v = illuminance / 10.0;
            String message = "Light:: " + v + " Lux";
            out.println(message);
            //print to LCD
            lcd20x4.printLine(1, message);
        });

        multiTouch.addTouchStateListener(touchState -> {
            String str = "";
            if((touchState & (1 << 12)) == (1 << 12)) { str += "In proximity.. "; }
            if ((touchState & 0xfff) == 0) {
                str += "No electrodes touched" + System.getProperty("line.separator");
            } else {
                str += "Electrodes ";
                for (int i = 0; i < 12; i++) {
                    if ((touchState & (1 << i)) == (1 << i)) {
                        str += i + " ";
                        try {
                            if (i == 3) {
                                System.out.println("3  will inc callbackrate 100ms ... ");
                                long callbackPeriod = ambientLight.getIlluminanceCallbackPeriod() + 100;
                                ambientLight.setIlluminanceCallbackPeriod(callbackPeriod);
                                lcd20x4.printLine(0, "dT " + callbackPeriod + " ms");
                            }
                            if (i == 5){
                                System.out.println("3  will dec callbackrate 100ms ... ");
                                long callbackPeriod = ambientLight.getIlluminanceCallbackPeriod() - 100;
                                ambientLight.setIlluminanceCallbackPeriod(callbackPeriod);
                                lcd20x4.printLine(0, "dT " + callbackPeriod + " ms");
                            }
                        } catch (TimeoutException | NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                str += "touched" + System.getProperty("line.separator");
            }
            System.out.println(str);
        });

        out.println("Press key to exit");
        System.in.read();
        ipcon.disconnect();
    }
}
