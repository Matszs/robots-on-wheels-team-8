//
//  wall-stop.c
//
//  Created by Mats Otten on 23-11-15.
//  Copyright (c) 2015 Mats Otten. All rights reserved.
//

#include  <stdio.h>
#include  <unistd.h>
#include  <wiringPi.h>
#include  <time.h>

void *wallStopHandler(void *arg);

void wallStopInit() {
	pthread_t wallStopThread;
	pthread_create(&wallStopThread, NULL, wallStopHandler, NULL);
}

void *wallStopHandler(void *arg) {
    while(1) {
        // TODO: Stop thread when automaticStop is 0 and re-start thread when enabled.
        if(automaticStop) {
            int distance = distanceRead();
            printf("Distance: %d\n", distance);
            printf("isDriving: %d\n", isDriving);
            if(isDriving && distance < 50 && distance > 0) {
                if(hasToStop == 0)
                    MotorcontrolMovement(0, 0, 0, 0); // stop driving
                hasToStop = 1;

                char wallStop[100];
                wallStop[0] = 1;
                writeToSocket(OPT_VIBRATE, &wallStop[0]);
            } else {
                hasToStop = 0;
            }
        }

        usleep(20000); // 200 micro seconds ????
    }
}


