
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
        
    Ellipse ring3 = EllipseBuilder.create().radiusX(45) .radiusY(45) .fill(Color.gray(1)).stroke(Color.BLACK) .build();

    Ellipse joystick = EllipseBuilder.create().radiusX(30).radiusY(30).fill(Color.RED).build();
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
        if (event.getY() < y){
            System.out.print("Vooruit, ");
        }
        if (event.getY() > y){
            System.out.print("Achteruit, ");
        }
        if (event.getX() > x){
            System.out.print("Rechts, ");
        }
        if (event.getX() < x){
            System.out.print("Links, ");
        }        

            CalculateData data = CalculateEngineValue.calculate(event.getX(), event.getY());
            System.out.println(data.deg);
        System.out.println(data.dis);
        //Display direction and percentage of full capacity.
            
    });
    
    pane.setOnMouseReleased ((MouseEvent event) -> {
        joystick.setCenterX(x);
        joystick.setCenterY(y);
        
        System.out.println("Stop, "+(x-x)+","+ (y-y));
    });

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
