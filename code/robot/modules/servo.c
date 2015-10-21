//
//  servo.c
//  robot
//
//  Created by ekko scholtens on 06/10/2015.
//  Copyright © 2015 Mats Otten. All rights reserved.
//

#include  <stdio.h>
#include  <unistd.h>
#include  <wiringPi.h>
#include  <softPwm.h>
#include  <time.h>

void *setServo();

void servoInit() {
	pthread_t servoThread;
	pthread_create(&servoThread, NULL, setServo, NULL);
}

void *setServo (){
	wiringPiSetup();
	pinMode(1,OUTPUT);
	digitalWrite(1,LOW);
	pwmSetClock(100);  //add  explanation
	softPwmCreate(1,0,100); //add explanation
	
	softPwmWrite(1,19);

	while (1) {
//		softPwmWrite(1,19);
		sleep(10);
	}
}
