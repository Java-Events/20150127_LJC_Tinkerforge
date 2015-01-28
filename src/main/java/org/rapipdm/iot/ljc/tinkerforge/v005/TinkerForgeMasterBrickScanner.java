package org.rapipdm.iot.ljc.tinkerforge.v005;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import org.rapipdm.iot.ljc.tinkerforge.WaitForQ;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tinkerforge.IPConnection.CONNECTION_STATE_CONNECTED;

/**
 * Created by Sven Ruppert on 21.02.14.
 */
public class TinkerForgeMasterBrickScanner {

    public static final String NETWORK = "192.168.0.";

    public static void main(String[] args) {
        IntStream.range(105, 110).boxed().parallel().filter(ip -> {
            try {

                final String host = NETWORK + ip;
                System.out.println("will try host = " + host);
                Socket so = new Socket();
                final InetSocketAddress endpoint = new InetSocketAddress(host, 4223);
                so.connect(endpoint, 100);
                final boolean connected = so.isConnected();
                so.close();
                return connected;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }).map(v -> {
            IPConnection ipConnection = new IPConnection();
            ipConnection.setTimeout(250);
            try {
                final String host = NETWORK + v;
                System.out.println("host = " + host);
                ipConnection.connect(host, 4223);
            } catch (IOException | AlreadyConnectedException e) {
                e.printStackTrace();
            }
            System.out.println("ipConnection = " + ipConnection);
            return ipConnection;
        }).filter(c -> c.getConnectionState() == CONNECTION_STATE_CONNECTED)
                .collect(Collectors.toList()).forEach(con -> {
            con.addEnumerateListener((uid, connectedUid, position, hardwareVersion, firmwareVersion, deviceIdentifier, enumerationType) -> {
                System.out.println("UID:               " + uid);
                System.out.println("Enumeration Type:  " + enumerationType);
                System.out.println("Connected UID:     " + connectedUid);
                System.out.println("Position:          " + position);
                System.out.println("Hardware Version:  " + hardwareVersion[0] + "." + hardwareVersion[1] + "." + hardwareVersion[2]);
                System.out.println("Firmware Version:  " + firmwareVersion[0] + "." + firmwareVersion[1] + "." + firmwareVersion[2]);
                System.out.println("Device Identifier: " + deviceIdentifier);
                System.out.println("");
            });
            try {
                con.enumerate();
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }

        });

        WaitForQ waitForQ = new WaitForQ();

        waitForQ.waitForQ();


    }
}
