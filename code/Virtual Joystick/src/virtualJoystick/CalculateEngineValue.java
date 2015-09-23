/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package virtualJoystick;
/**
 *
 * @author Suzanne
 */
public class CalculateEngineValue {

    public static CalculateData calculate(double x, double y) {
        double x1 = 150;
        double y1 = 150;
        double deltaX = x - x1;
        double deltaY = y - y1;
        double rad = Math.atan2(deltaY, deltaX);
        
        rad += Math.PI/2;
        double deg = Math.toDegrees(rad);
            if (deg < 0) {
            deg += 360;
            }
            
        double pointDelta = Math.pow(deltaX,2)+ Math.pow(deltaY, 2);
            
        double dis = Math.sqrt(pointDelta); 
        
            return new CalculateData(deg, dis);
    }
    
}
