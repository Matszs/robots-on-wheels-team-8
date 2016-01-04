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
int fd;
int dataAvailable;
int currentCharacter;

char start[1] = "\x02";   //start flag
char stop[1] ="\x03";      //stop flag

char current_buffer[100], old_buffer[100];
int i, j, dataAvailable, currentCharacter;

void lineFollowerInit() {
	wiringPiSetup();
	fd = serialOpen(SERIAL_DEV, SPEED);
	printf("USB: %d \n", fd);
	serialFlush(fd);

	printf("line follower");

	pthread_t lineFollowReaderThread;
    pthread_create(&lineFollowReaderThread, NULL, lineFollowReader, NULL);
}

void *lineFollowReader(void *arg) {
	for (;;) {
		dataAvailable = serialDataAvail(fd);
		if (dataAvailable >= 12) {
			j=0;
			for (i=0; i < dataAvailable; i++) {
				currentCharacter = serialGetchar(fd);
				if ( currentCharacter == start[0]) {
					for(;;)	{
						currentCharacter = serialGetchar(fd);
						if (currentCharacter == stop[0]) {
							current_buffer[j] = '\0';
							break;
						} else {
							current_buffer[j] = currentCharacter;
							j++;
						}
					}
					if (strcmp(current_buffer,old_buffer) != 0) {
						printf("%i Characters buffered: %s\n",j, current_buffer);
						strcpy(old_buffer, current_buffer);
					}
					serialFlush(fd);
					break;
				}
			}
		}
		usleep(100000);
	}
}

