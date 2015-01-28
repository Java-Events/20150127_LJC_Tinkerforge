package org.rapipdm.iot.ljc.tinkerforge.pong;

import java.io.IOException;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;

public class TinkerPong extends Application
{
	private static final int WINDOW_WIDTH = 640;
	private static final int WINDOW_HEIGHT = 512;
	private static final int UI_REFRESH_MILLIS = 40; // 25fps

	private Canvas canvas;
	private GraphicsContext gc;

	private double ballX;
	private double ballY;
	private double ballDiameter = 80;
	private int ballColourGB = 255;

	private static final double INITIAL_BALL_SPEED = 10;

	private double ballDirX = INITIAL_BALL_SPEED;
	private double ballDirY = INITIAL_BALL_SPEED;

	private double batWidth = 20;
	private double batHeight = 150;
	private double batSpeed = INITIAL_BALL_SPEED;
	private double batY = 0;

	private double canvasWidth = WINDOW_WIDTH;
	private double canvasHeight = WINDOW_HEIGHT;

	private int score = 0;

	private IPConnection ipcon;

	private static final String HOST = "localhost";
	private static final int PORT = 4223;

	private static final String UID = "pcm";
	private BrickletMultiTouch mt;
	private int keyTouched = 0;

	public static void main(String[] args)
	{
		launch(args);
	}

	public TinkerPong()
	{
		connectBrick();

		setupTouch();
	}

	private void connectBrick()
	{
		ipcon = new IPConnection();

		try
		{
			ipcon.connect(HOST, PORT);
		}
		catch (AlreadyConnectedException | IOException e)
		{
			e.printStackTrace();
		}
	}

	private void setupTouch()
	{
		mt = new BrickletMultiTouch(UID, ipcon);

		mt.addTouchStateListener(new BrickletMultiTouch.TouchStateListener()
		{
			@Override
			public void touchState(int touchState)
			{
				if ((touchState & 0xfff) == 0)
				{
					keyTouched = 0;
				}
				else
				{
					for (int i = 0; i < 12; i++)
					{
						if ((touchState & (1 << i)) == (1 << i))
						{
							keyTouched = i;
							break;
						}
					}
				}
			}
		});
	}

	@Override
	public void start(final Stage stage)
	{
		stage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent arg0)
			{
				try
				{
					ipcon.disconnect();
				}
				catch (NotConnectedException e)
				{
					e.printStackTrace();
				}
			}
		});

		canvas = new Canvas(canvasWidth, canvasHeight);
		gc = canvas.getGraphicsContext2D();

		gc.setFont(new Font("Courier New", 36));

		BorderPane borderPane = new BorderPane();

		borderPane.setCenter(canvas);

		Scene scene = new Scene(borderPane, WINDOW_WIDTH, WINDOW_HEIGHT);

		stage.setTitle("TinkerPong!");
		stage.setScene(scene);
		stage.show();

		final Duration oneFrameAmt = Duration.millis(UI_REFRESH_MILLIS);

		resetGame();

		final KeyFrame oneFrame = new KeyFrame(oneFrameAmt, new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent arg0)
			{
				checkBatMovement();

				moveBall();

				redraw();
			}
		});

		TimelineBuilder.create().cycleCount(Animation.INDEFINITE).keyFrames(oneFrame).build().play();
	}

	private void checkBatMovement()
	{
		switch (keyTouched)
		{
		case 10:
			batY -= batSpeed;
			batY = Math.max(batY, 0);
			break;
		case 4:
			batY += batSpeed;
			batY = Math.min(batY, canvasHeight - batHeight);
			break;
		}
	}

	private void moveBall()
	{
		ballX += ballDirX;
		ballY += ballDirY;

		if (ballX + ballDiameter >= canvasWidth)
		{
			ballDirX = -ballDirX;
		}
		else if (ballX <= batWidth)
		{
			double midBall = ballY + ballDiameter / 2;

			double tolerance = batHeight * 0.25;

			double minHit = batY - tolerance;
			double maxHit = batY + batHeight + tolerance;

			if (midBall >= minHit && midBall <= maxHit)
			{
				hitBall();
			}
			else
			{
				resetGame();
			}
		}

		if (ballY + ballDiameter >= canvasHeight)
		{
			ballDirY = -ballDirY;
		}
		else if (ballY <= 0)
		{
			ballDirY = -ballDirY;
		}
	}

	private void hitBall()
	{
		ballDirX = -ballDirX;
		score++;

		ballX = batWidth;

		// increase speed by 5%
		ballDirX *= 1.05;
		ballDirY *= 1.05;

		// ball gets redder!
		ballColourGB -= 10;
		ballColourGB = Math.max(0,  ballColourGB);
	}

	private void resetGame()
	{
		score = 0;

		ballX = canvasWidth / 2 - ballDiameter / 2;
		ballY = canvasHeight / 2 - ballDiameter / 2;

		batY = canvasHeight  / 2 - batHeight / 2;

		ballDirX = INITIAL_BALL_SPEED;
		ballDirY = INITIAL_BALL_SPEED;

		ballColourGB = 255;
	}

	private void redraw()
	{
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvasWidth, canvasHeight);

		gc.setFill(Color.GREEN);
		gc.fillRect(0, batY, batWidth, batHeight);

		gc.setFill(Color.rgb(255, ballColourGB, ballColourGB));
		gc.fillOval(ballX, ballY, ballDiameter, ballDiameter);

		gc.setFill(Color.WHITE);
		gc.fillText("Score: " + score, 0, 30);
	}
}
