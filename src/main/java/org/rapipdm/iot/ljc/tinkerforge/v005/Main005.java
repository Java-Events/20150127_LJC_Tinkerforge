package org.rapipdm.iot.ljc.tinkerforge.v005;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import org.rapipdm.iot.ljc.tinkerforge.Localhost;

import java.io.IOException;

/**
 * Created by sven on 28.01.15.
 */
public class Main005 {
    public static void main(String[] args) throws AlreadyConnectedException, IOException, NotConnectedException, InterruptedException {

        final IPConnection ipConnection = new IPConnection();
        ipConnection.connect(Localhost.HOST, Localhost.PORT);
        ipConnection.addEnumerateListener((uid, connectedUid, position, hardwareVersion, firmwareVersion, deviceIdentifier, enumerationType) -> {
            System.out.println("UID:               " + uid);
            System.out.println("Enumeration Type:  " + enumerationType);
            System.out.println("Connected UID:     " + connectedUid);
            System.out.println("Position:          " + position);
            System.out.println("Hardware Version:  " + hardwareVersion[0] + "." + hardwareVersion[1] + "." + hardwareVersion[2]);
            System.out.println("Firmware Version:  " + firmwareVersion[0] + "." + firmwareVersion[1] + "." + firmwareVersion[2]);
            System.out.println("Device Identifier: " + deviceIdentifier);
            System.out.println("");

        });
        ipConnection.enumerate();
        Thread.sleep(1_000);
    }
}
