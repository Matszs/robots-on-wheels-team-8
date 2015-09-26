/* This code was created by Suzanne Peerdeman, IT201, and is therefore creative
property of the HVA (University of applied sciences, Amsterdam). Being creative 
property of the HVA, this code may be used by all students, please do not remove 
this header!
*/
package virtualJoystick;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.EllipseBuilder;
import javafx.stage.Stage;



public class Joystick extends Application {
    
@Override
public void start(Stage primaryStage) throws Exception {
    Pane pane = new Pane();
    
        double x = 150;
        double y = 150;
        double maxX = x+100;
        double maxY = y+100;
        double minX = x-100;
        double minY = y-100; 
        
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

        //Builds ellipse
pane.setOnMouseDragged((MouseEvent event) -> {
        double newX = event.getX();
        if (newX > maxX || newX < minX) {
            return;
        }
        
        double newY = event.getY();
        if (newY > maxY|| newY < minY) {
            return;
        }
        //Declares bandraries for virtual joystick        
        joystick.setCenterX(event.getX());
        joystick.setCenterY(event.getY());
        //Sets x, y coordinates of joystickknob to x, y of mouse, displays as written output.
////////////////////////////////////////////////////////////////////////////////////////////
        
        
    CalculateData data = CalculateEngineValue.calculate(event.getX(), event.getY());
        //Calculates direction and speed of cart.
    CalculateDisDeg d = DisDeg.vlakken(data.dis, data.deg);
    System.out.println(d.distance + ", " + d.degree);
 //uses CalculateEngineValue, CalculateData, CalculateDisDeg and Disdeg.                   
    });
////////////////////////////////////////////////////////////////////////////////////////////
    
    pane.setOnMouseReleased ((MouseEvent event) -> {
        joystick.setCenterX(x);
        joystick.setCenterY(y);
        
    });
///////////////////////////////////////////////////////////////////////////////////////////
    
    Scene scene = SceneBuilder.create().root(pane).width(300d).height(300d).build();
    primaryStage.setScene(scene);

    primaryStage.show();
}


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        

                
    }
    
}
