//
//  speed.c
//  robot
//
//  Created by ekko scholtens on 29/09/2015.
//  Copyright © 2015 Mats Otten. All rights reserved.
//
// ticks 20 = 220 mm = 1 revolution

#include  <time.h>
#include  <wiringPi.h>
#include  <stdio.h>
#include  <math.h>

int ticks = 0;
double speed = 0;
void interrupt (void) { ticks++; }
void *measureSpeed();


void speedInit() {
	wiringPiSetup () ;
	pullUpDnControl(0,PUD_DOWN);
	
	wiringPiISR (0, INT_EDGE_RISING, &interrupt) ;
	
	pthread_t speedThread;
	pthread_create(&speedThread, NULL, measureSpeed, NULL);
}

void *measureSpeed (){
	while (1) {
		sleep(1);
		speed = (11.0 * ticks) / 1000;
		ticks = 0;
	}
}

double speedRead(){
	return speed;
}