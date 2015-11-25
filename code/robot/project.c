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
	if(opcode == 1) {
		void (*motorCallback)(uint8_t,uint8_t,uint8_t,uint8_t) = MotorcontrolMovement;
		movement direction;

		unpackMovement((uint8_t)commandData[0], &direction);
		MotorControl(&direction, *motorCallback);
	} else if(opcode == 8) {
	    automaticStop = (uint8_t)(commandData[0]);
	    printf("stop: %d", automaticStop);
	} else if(opcode == OPT_LICENSE) {
	    //system("cd /home/pi/cam && fswebcam -r 1920x1080 -S 20 --no-banner --quiet alpr.jpg && tesseract -psm 7 alpr.jpg stdout");
        system("sudo /etc/init.d/robot stop-cam");

	    FILE *ls = popen("cd /home/pi/cam && fswebcam -r 1920x1080 -S 1 -D 1 --no-banner --quiet alpr.jpg && tesseract -psm 7 alpr.jpg stdout", "r");
        char buf[100];
        while (fgets(buf, sizeof(buf), ls) != 0) {
            printf("%s", buf);
            writeToSocket(OPT_LICENSE, &buf[0]);
        }
        system("sudo /etc/init.d/robot start-cam");

        pclose(ls);
	}
	// TODO: add engine ...
}

void onDisconnect() {
	MotorcontrolMovement(0, 0, 0, 0); // stop driving
}