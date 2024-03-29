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

#include <stdio.h>
#include <string.h> //strlen
#include <unistd.h> // write
#include <sys/socket.h>
#include <arpa/inet.h> //inet_addr
#include <pthread.h> // threads
#include <wiringPi.h>
#include <time.h>
//#include  "rpiGpio.h" // remove when WiringPi fully included

#include  <softPwm.h>

#include "modules/socket.c"
#include "modules/motor.c"
#include "modules/distance.c"
#include "modules/speed.c"
#include "modules/compass.c"
#include "modules/servo.c"

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
		usleep(100000);
    }
}

void onCommand(uint8_t opcode, char *commandData) {
	if(opcode == 1) {
		void (*motorCallback)(uint8_t,uint8_t,uint8_t,uint8_t) = MotorcontrolMovement;
		movement direction;

		unpackMovement((uint8_t)commandData[0], &direction);
		MotorControl(&direction, *motorCallback);
	}
	// TODO: add engine ...
}

void onDisconnect() {
	MotorcontrolMovement(0, 0, 0, 0); // stop driving
}