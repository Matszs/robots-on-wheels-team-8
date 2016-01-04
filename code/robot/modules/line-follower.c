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

void lineTracking(int led1, int led2, int led3, int led4) {
	printf("> %d, %d, %d, %d <\n", led1, led2, led3, led4);
}

void *lineFollowReader(void *arg) {
	for (;;) {
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
		usleep(100000);
	}
}

