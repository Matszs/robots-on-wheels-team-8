//
//  line-follower.c
//
//  Created by Mats Otten on 14-09-15.
//  Copyright (c) 2015 Mats Otten. All rights reserved.
//

#include  <stdio.h>
#include  <unistd.h>
#include  <time.h>
#include <wiringSerial.h>

#define SERIAL_DEV "/dev/ttyACM0"
#define SPEED 9600

void *lineFollowReader(void *arg);

int fdArduino;
int dataAvailable;
int currentCharacter;

char start[1] = "\x02";   //start flag
char stop[1] ="\x03";      //stop flag
char breakPoint[1] =",";      //stop flag

char current_buffer[100], old_buffer[100];
int i, j, dataAvailable, currentCharacter;

void lineFollowerInit() {
	wiringPiSetup();
	fdArduino = serialOpen(SERIAL_DEV, SPEED);
	printf("USB: %d \n", fdArduino);

	if(fdArduino >= 0) {
		serialFlush(fdArduino);

		pthread_t lineFollowReaderThread;
    	pthread_create(&lineFollowReaderThread, NULL, lineFollowReader, NULL);
    }
}

//IR1 = middle front
//IR2 = right
//IR3 = middle back
//IR4 = left

int isRotating = 0;

void lineTracking(int led1, int led2, int led3, int led4) {


	int whiteDetectionLed1 = (led1 > 1);
	int whiteDetectionLed2 = (led2 > 1);
	int whiteDetectionLed3 = (led3 > 1);
	int whiteDetectionLed4 = (led4 > 1);

	printf("> %d, %d, %d, %d <\n", whiteDetectionLed1, whiteDetectionLed2, whiteDetectionLed3, whiteDetectionLed4);


	if(!whiteDetectionLed1) {
		movement direction;
		unpackMovement((uint8_t)17, &direction);
		MotorcontrolMovement(&direction);
		isRotating = 0;
	} else if(isDriving && !isRotating) {
		movement direction;
		unpackMovement((uint8_t)204, &direction);
        MotorcontrolMovement(&direction);
        usleep(50000);
		unpackMovement((uint8_t)0, &direction);
		MotorcontrolMovement(&direction);
		isRotating = 1;
	}

	if(isRotating && whiteDetectionLed1 && !whiteDetectionLed4) {
		movement direction;
		unpackMovement((uint8_t)196, &direction);
		MotorcontrolMovement(&direction);
		printf("LEFT");
	}

	if(isRotating && whiteDetectionLed1 && !whiteDetectionLed2) {
		movement direction;
		unpackMovement((uint8_t)76, &direction);
		MotorcontrolMovement(&direction);
		printf("RIGHT");
	}




}

void *lineFollowReader(void *arg) {
	for (;;) {
		if(!isConnected || !lineFollowingEnabled) { // check if socket connected.
			usleep(100000);
			continue;
		}

		dataAvailable = serialDataAvail(fdArduino);
		if (dataAvailable >= 12) {
			j=0;
			for (i=0; i < dataAvailable; i++) {
				currentCharacter = serialGetchar(fdArduino);
				if ( currentCharacter == start[0]) {
					for(;;)	{
						currentCharacter = serialGetchar(fdArduino);
						if(currentCharacter == breakPoint[0]) {
							current_buffer[j] = ' ';
                            j++;
						}else if (currentCharacter == stop[0]) {
							current_buffer[j] = '\0';
							break;
						} else {
							current_buffer[j] = currentCharacter;
							j++;
						}
					}
					if (strcmp(current_buffer,old_buffer) != 0) {
						if(DEBUG)
							printf("%i Characters buffered: %s\n",j, current_buffer);
						strcpy(old_buffer, current_buffer);
					}

					char * pEnd;
					int led1, led2, led3, led4;

					led1 = strtol (current_buffer, &pEnd, 10);
                    led2 = strtol (pEnd, &pEnd, 10);
                    led3 = strtol (pEnd, &pEnd, 10);
                    led4 = strtol (pEnd, NULL, 10);

					lineTracking(led1, led2, led3, led4);

					serialFlush(fdArduino);
					break;
				}
			}
		}
		usleep(10);
	}
}

