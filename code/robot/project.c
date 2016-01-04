//
//  main.c
//  row-server
//
//  Created by Mats Otten on 14-09-15.
//  Copyright (c) 2015 Mats Otten. All rights reserved.

// sudo gcc -o project project.c -L/usr/local/lib  -lwiringPi -lwiringPiDev -lpthread -lm -lrpigpio

#define PORT_NUMBER		1212
#define DEBUG			0

#define OPT_DEFAULT		0
#define OPT_MOTOR		1
#define OPT_DISTANCE	2
#define OPT_SPEED		3
#define OPT_CAMERA		4
#define OPT_SERVO		5
#define OPT_COMPASS		6
#define OPT_WALL_STOP	8
#define OPT_VIBRATE		9
#define OPT_LICENSE		7

#include <stdio.h>
#include <string.h> //strlen
#include <unistd.h> // write
#include <sys/socket.h>
#include <arpa/inet.h> //inet_addr
#include <pthread.h> // threads
#include <wiringPi.h>
#include <time.h>

int automaticStop = 0;
int isDriving = 0;
int hasToStop = 0;
//#include  "rpiGpio.h" // remove when WiringPi fully included

#include  <softPwm.h>

#include "modules/socket.c"
#include "modules/motor.c"
#include "modules/distance.c"
#include "modules/speed.c"
#include "modules/compass.c"
#include "modules/servo.c"
#include "modules/wall-stop.c"
#include "modules/license_plate.c"
#include "modules/line-follower.c"

int main() {
    setvbuf(stdout, NULL, _IONBF, 0); // display printf's

    run();
    return 0;
}

void run() {
	socketInit();
	MotorInit();
	distanceInit();
	speedInit();
	compassInit();
	wallStopInit();
	licensePlateInit();
	//lineFollowerInit();
//	servoInit();

    while(1) {
       //printf("afstand: %d, speed: %f\n", distanceRead(), speedRead());
		char speed[100];
		sprintf(speed, "%d", speedRead());
		writeToSocket(OPT_SPEED,  &speed[0]);

		char compass[100];
		sprintf(compass, "%d", compassRead());
		writeToSocket(OPT_COMPASS,  &compass[0]);

		char distance[100];
		sprintf(distance, "%d", distanceRead());
		writeToSocket(OPT_DISTANCE,  &distance[0]);

        char autoStopValue[100];
		sprintf(autoStopValue, "%d", automaticStop);
		writeToSocket(OPT_WALL_STOP, &autoStopValue[0]);

		usleep(100000);
    }
}

void onCommand(uint8_t opcode, char *commandData) {
	if(opcode == OPT_MOTOR) {
		movement direction;

		unpackMovement((uint8_t)commandData[0], &direction);
		MotorcontrolMovement(&direction);
	} else if(opcode == OPT_WALL_STOP) {
	    automaticStop = (uint8_t)(commandData[0]);
	    printf("stop: %d", automaticStop);
	} else if(opcode == OPT_LICENSE) {
		licensePlateReader();
	}
	// TODO: add engine ...
}

void onDisconnect() {
	movement direction;
	
	unpackMovement((uint8_t)0, &direction);
	MotorcontrolMovement(&direction);
}