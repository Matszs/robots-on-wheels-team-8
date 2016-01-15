//
//  distance.c
//
//  Created by Mats Otten on 14-09-15.
//  Copyright (c) 2015 Mats Otten. All rights reserved.
//

#include  <stdio.h>
#include  <unistd.h>
#include  <wiringPi.h>
#include  <softPwm.h>
#include  <time.h>

void *distancePerformRead(void *arg);
int distanceValue = 50;
int distanceSetup;

void distanceInit() {
	distanceSetup = wiringPiI2CSetup(0x70);

	pthread_t distanceThread;
	pthread_create(&distanceThread, NULL, distancePerformRead, NULL);
}

int distanceRead() {
	return distanceValue;
}

void *distancePerformRead(void *arg) {
    uint8_t TxData1[2] = {00, 81}; //add second array element; read the datasheet of the srf02
    uint8_t TxData2[1] = {0x03}; //What is the function of memory location 3?; see datasheet srf02

    while(1) {
       wiringPiI2CWriteReg8(distanceSetup, 0, 81);
       usleep(250000);
       distanceValue = wiringPiI2CReadReg8(distanceSetup, 3);
     }
}


