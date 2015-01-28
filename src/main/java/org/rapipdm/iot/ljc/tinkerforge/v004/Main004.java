package org.rapipdm.iot.ljc.tinkerforge.v004;

import com.tinkerforge.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.rapipdm.iot.ljc.tinkerforge.Localhost;

import java.io.IOException;
import java.util.Date;

/**
 * Created by sven on 28.01.15.
 */
public class Main004 extends Application {

    private static final String UID_AMBIENT_LIGHT = "mbL"; // Change to your UID

    private static BrickletAmbientLight ambientLight;
    private static IPConnection ipcon;

    public static void main(String args[]) throws Exception {
        launch(args);
        ipcon.disconnect();
    }

    private static void initTinkerForge() throws IOException, AlreadyConnectedException, TimeoutException, NotConnectedException {
        ipcon = new IPConnection();
        ipcon.connect(Localhost.HOST, Localhost.PORT);
        ambientLight = new BrickletAmbientLight(UID_AMBIENT_LIGHT, ipcon);
        ambientLight.setIlluminanceCallbackPeriod(1_000);
        ambientLight.addIlluminanceListener(illuminance -> {
            double value = illuminance / 10.0;
            Platform.runLater(() -> {
                final XYChart.Data data = new XYChart.Data(new Date(), value);
                series.getData().add(data);
            });
        });
    }

    public static XYChart.Series series;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Line Chart TinkerForge Sample");
        final DateAxis dateAxis = new DateAxis();
        final NumberAxis yAxis = new NumberAxis();
        dateAxis.setLabel("Time of Lux");
        final LineChart lineChart = new LineChart<>(dateAxis, yAxis);

        lineChart.setTitle("LUX Monitoring");

        series = new XYChart.Series();
        series.setName("My Light ;-)");

        lineChart.getData().add(series);
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);

        initTinkerForge();

        stage.show();
    }
}
