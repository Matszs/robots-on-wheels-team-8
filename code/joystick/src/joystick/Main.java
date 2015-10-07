//
//  Main.java
//
//  Created by Suzanne Peerderman on 14-09-15.
//  Copyright (c) 2015 Suzanne Peerderman. All rights reserved.
//

package joystick;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.EllipseBuilder;
import javafx.stage.Stage;
import joystick.classes.*;
import joystick.listeners.DataReceiveListener;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
		final SocketClient sc = new SocketClient();
		sc.setUp();

		sc.addListener(new DataReceiveListener() {
			@Override
			public void onDataReceive(int module, byte[] data) {
				try {
					System.out.println("Module: " + module);
					System.out.println("Data: " + new String(data, "UTF-8"));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});

		Pane pane = new Pane();

		final double x = 150;
		final double y = 150;
		final double maxX = x + 100;
		final double maxY = y + 100;
		final double minX = x - 100;
		final double minY = y - 100;

		final Ellipse center = EllipseBuilder.create().centerX(x).centerY(y).radiusX(5).radiusY(5).fill(Color.BLACK).build();
		pane.getChildren().add(center);
		final Ellipse ring1 = EllipseBuilder.create().centerX(x).centerY(y).radiusX(15).radiusY(15).fill(null).stroke(Color.BLACK).build();
		pane.getChildren().add(ring1);
		final Ellipse ring2 = EllipseBuilder.create().centerX(x).centerY(y).radiusX(30).radiusY(30).fill(null).stroke(Color.BLACK).build();
		pane.getChildren().add(ring2);
		final Ellipse ring3 = EllipseBuilder.create().centerX(x).centerY(y).radiusX(45).radiusY(45).fill(null).stroke(Color.BLACK).build();
		pane.getChildren().add(ring3);
		final Ellipse ring4 = EllipseBuilder.create().centerX(x).centerY(y).radiusX(60).radiusY(60).fill(null).stroke(Color.BLACK).build();
		pane.getChildren().add(ring4);
		final Ellipse ring5 = EllipseBuilder.create().centerX(x).centerY(y).radiusX(75).radiusY(75).fill(null).stroke(Color.BLACK).build();
		pane.getChildren().add(ring5);
		final Ellipse ring6 = EllipseBuilder.create().centerX(x).centerY(y).radiusX(90).radiusY(90).fill(null).stroke(Color.BLACK).build();
		pane.getChildren().add(ring6);

		final Ellipse joystick = EllipseBuilder.create().radiusX(30).radiusY(30).fill(Color.RED).build();
		pane.getChildren().add(joystick);
		joystick.setCenterX(x);
		joystick.setCenterY(y);

		pane.setOnMouseDragged(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				double newX = event.getX();
				if (newX > maxX || newX < minX) {
					return;
				}

				double newY = event.getY();
				if (newY > maxY || newY < minY) {
					return;
				}

				joystick.setCenterX(event.getX());
				joystick.setCenterY(event.getY());

				int value = Calculation.calculateValue(event.getX(), event.getY());
				try {
					sc.write(1, new byte[] { (byte)value });
					System.out.println(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		pane.setOnMouseReleased(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				joystick.setCenterX(x);
				joystick.setCenterY(y);

				try {
					sc.write(1, new byte[] { 0 });
					System.out.println("STOP");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		Scene scene = SceneBuilder.create().root(pane).width(300d).height(300d).build();
		primaryStage.setScene(scene);

		primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
