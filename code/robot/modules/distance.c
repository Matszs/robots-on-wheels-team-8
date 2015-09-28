//
//  distance.c
//
//  Created by Mats Otten on 14-09-15.
//  Copyright (c) 2015 Mats Otten. All rights reserved.
//

#include  <stdio.h>
#include  <unistd.h>
#include  "rpiGpio.h"
#include  <wiringPi.h>
#include  <softPwm.h>
#include  <time.h>

void *distancePerformRead(void *arg);
uint8_t distanceValue = 50;
int distanceSetup;

void writeDataDistance(uint8_t * data, int lenght){
    int i;
	for (i = 0; i < lenght; i++) {
		int test = wiringPiI2CWrite(distanceSetup, data[i]);
		printf("TEST: %d   Data: %d  \n", test, data[i]);
	}
}

void distanceInit() {
	distanceSetup = wiringPiI2CSetup(0x70);

	pthread_t distanceThread;
	pthread_create(&distanceThread, NULL, distancePerformRead, NULL);
}

uint8_t distanceRead() {
	return distanceValue;
}

void *distancePerformRead(void *arg) {
    uint8_t TxData1[2] = {00, 81}; //add second array element; read the datasheet of the srf02
    uint8_t TxData2[1] = {0x03}; //What is the function of memory location 3?; see datasheet srf02

    while(1) {
       wiringPiI2CWriteReg8(distanceSetup, 0, 81);
       sleep(0.25);
       distanceValue = wiringPiI2CReadReg8(distanceSetup, 3);
     }
}


