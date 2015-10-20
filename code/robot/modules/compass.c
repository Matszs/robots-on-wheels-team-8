//
//  compass.c
//  robot
//
//  Created by ekko scholtens on 05/10/2015.
//  Copyright © 2015 Mats Otten. All rights reserved.
//
#define _USE_MATH_DEFINES
#include  <stdio.h>
#include  <unistd.h>
#include <math.h>
#include <time.h>
#include  <wiringPi.h>

int angelCompass;
int compass;
void *compassPerformRead();


void compassInit(){
	compass = wiringPiI2CSetup(0x1e);
	wiringPiI2CWriteReg8(compass, 0x00, 0x70);
	wiringPiI2CWriteReg8(compass, 0x01, 0xA0);
	wiringPiI2CWriteReg8(compass, 0x02, 0x01);
	pthread_t compassThread;
	pthread_create(&compassThread, NULL, compassPerformRead, NULL);

}

uint8_t compassRead() {
	return angelCompass;
}

void *compassPerformRead(){
	
	while(1){
		wiringPiI2CWriteReg8(compass, 0x02, 0x01); //idle mode
		usleep(100000);
		int xh = wiringPiI2CReadReg8(compass, 0x03);
		int xl = wiringPiI2CReadReg8(compass, 0x04);
		int yh = wiringPiI2CReadReg8(compass, 0x07);
		int yl = wiringPiI2CReadReg8(compass, 0x08);
		int zh = wiringPiI2CReadReg8(compass, 0x05);
		int zl = wiringPiI2CReadReg8(compass, 0x06);
		
		short x = (xh << 8) | xl;
		short y = (yh << 8) | yl;
		short z = (zh << 8) | zl;
		
		
		float angle = (atan2(y, x)) * 180 / M_PI;
//		printf("angle before = %0.1f ", angle);
		
		if (angle < 0) {
			angle += 360;
		}
		angelCompass = angle;
		sleep(1);
		
	}
}


