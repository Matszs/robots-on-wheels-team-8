//
//  license_plate.c
//
//  Created by Mats Otten on 6-12-15.
//  Copyright (c) 2015 Mats Otten. All rights reserved.
//

#include  <stdio.h>
#include  <unistd.h>
#include  <time.h>

void *licensePlateHandler(void *arg);

void licensePlateReader() {
	pthread_t licensePlateThread;
	pthread_create(&licensePlateThread, NULL, licensePlateHandler, NULL);
}

void *licensePlateHandler(void *arg) {
    //system("cd /home/pi/cam && fswebcam -r 1920x1080 -S 20 --no-banner --quiet alpr.jpg && tesseract -psm 7 alpr.jpg stdout");

	FILE *ls = popen("cd /home/pi/cam && wget http://localhost:8080?action=snapshot -O alpr.jpg > /dev/null 2>&1 && tesseract -psm 7 alpr.jpg stdout", "r");
	char buf[100];
	while (fgets(buf, sizeof(buf), ls) != 0) {
		printf("%s", buf);
		writeToSocket(OPT_LICENSE, &buf[0]);
	}

	pclose(ls);
}


