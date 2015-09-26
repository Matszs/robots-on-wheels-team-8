/* This code was created by Suzanne Peerdeman, IT201, and is therefore creative
property of the HVA (University of applied sciences, Amsterdam). Being creative 
property of the HVA, this code may be used by all students, please do not remove 
this header!
*/
package virtualJoystick;

public class DisDeg {
    public static CalculateDisDeg vlakken(double dis, double deg) {
        double distance = 0;
        double degree = 0;
        
                    if (dis < 15){
                    distance = 1;
                    }
                    if (dis > 14 & dis < 30){
                     distance = 2;
                    }  
                    if (dis > 29 & dis < 45){
                     distance = 3;
                    }        
                    if (dis > 44 & dis < 60){
                     distance = 4;
                    }
                    if (dis > 59 & dis < 75){
                     distance = 5;
                    }  
                    if (dis > 74){
                     distance = 6;
                    }
                    
                        if (deg < 15){
                             degree =1;
                        }
                        if (deg >= 15 & deg < 30){
                             degree =2;
                        }  
                        if (deg >= 30 & deg < 45){
                             degree =3;
                        }        
                        if (deg >= 45 & deg < 60){
                             degree =4;
                        }
                        if (deg >= 60 & deg < 75){
                             degree =5;
                        }  
                        if (deg < 90 & deg >= 75){
                             degree =6;
                        }
                        if (deg >= 90 & deg < 105){
                             degree =7;
                        }
                        if (deg >= 105 & deg < 120){
                             degree =8;
                        }  
                        if (deg >= 120 & deg < 135){
                             degree =9;
                        }        
                        if (deg >= 135 & deg < 150){
                             degree =10;
                        }
                        if (deg >= 150 & deg < 165){
                             degree =11;
                        }  
                        if (deg >= 165 & deg < 180){
                             degree =12;
                        }   
                        if (deg >= 180 & deg <195){
                             degree =13;
                        }
                        if (deg >= 195 & deg < 210){
                             degree =14;
                        }  
                        if (deg >= 210 & deg < 225){
                             degree =15;
                        }        
                        if (deg >= 225 & deg < 240){
                             degree =16;
                        }
                        if (deg >= 240 & deg < 255){
                             degree =17;
                        }  
                        if (deg >= 255 & deg < 270){
                             degree =18;
                        }
                        if (deg >= 270 & deg < 285){
                             degree =19;
                        }
                        if (deg >= 285 & deg < 300){
                             degree =20;
                        }  
                        if (deg >= 300 & deg < 315){
                             degree =21;
                        }        
                        if (deg >= 314 & deg < 330){
                             degree =22;
                        }
                        if (deg >= 330 & deg < 345){
                             degree =23;
                        }  
                        if (deg >= 345 & deg <= 360){
                             degree =24;
                        }

            
                    
                    
                    
         return new CalculateDisDeg (distance, degree);          
                    
    }
}

 
        
    

    

