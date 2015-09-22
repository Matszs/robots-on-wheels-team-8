//
//  control.c
//  Robot
//
//  Created by Patrick Hendriks on 14-09-15.
//  Copyright (c) 2015 Patrick Hendriks. All rights reserved.
//  Toegestaan om voor het Project Robot on Wheels vrij te gebruiken en of aan te passen.
//

#include "motor.h"
int fd;

void writeData(uint8_t * data, int lenght){
    int i;
	for (i = 0; i < lenght; i++) {
		printf("%d\n", data[i]);
		wiringPiI2CWrite(fd, data[i]);
	}
	printf("\n\n\n");
}

void unpackMovement(uint8_t getal, movement *direction){
    uint8_t links, rechts;

	//1010 1001
	//1001 0000
	//0000 1001
    rechts = (getal << 4) >> 4;
    links = getal >> 4;
    direction->Left = links;
    direction->Right = rechts;
}

void MotorControl(movement *direction, void (*motorCallback)(uint8_t,uint8_t,uint8_t,uint8_t)){
   // 0000 (links)  		0000 (rechts)
   // ^ richting			^ richting
   //  ^^^ snelheid			 ^^^ snelheid

   // 0001 0001

    uint8_t RijrichtingLinks = direction->Left >> 3 & 1;
    uint8_t RijrichtingRechts = direction->Right >> 3 & 1;
    uint8_t SnelheidLinks = direction->Left & 7;
    uint8_t SnelheidRechts = direction->Right & 7;

    uint8_t voorachterLinks = RijrichtingLinks;
    uint8_t voorachterRechts = RijrichtingRechts;


    motorCallback(voorachterLinks, SnelheidLinks, voorachterRechts, SnelheidRechts);
}

void MotorcontrolMovement(uint8_t voorachterLinks, uint8_t SnelheidLinks, uint8_t voorachterRechts, uint8_t SnelheidRechts){

    uint8_t richtingLinks = (voorachterLinks == 1) ? 1 : 2;
    uint8_t richtingRechts = (voorachterRechts == 1) ? 1 : 2;
    uint8_t MotorC[7];

    //Wanneer commando stop oftwel 0 gegeven wordt over de socket.
    if(richtingLinks == 2 && richtingRechts == 2 && SnelheidLinks == 0 && SnelheidRechts == 0)
    {

        MotorC[0] = 7;
        MotorC[1] = 0;
        MotorC[2] = 0;
        MotorC[3] = 0;
        MotorC[4] = 0;
        MotorC[5] = 0;
        MotorC[6] = 0;

    }
    else
    {

        MotorC[0] = 7;
        MotorC[1] = 3;
        MotorC[2] = speedTable[SnelheidLinks];
        MotorC[3] = (SnelheidLinks == 0 ? 0 : richtingLinks);
        MotorC[4] = 3;
        MotorC[5] = speedTable[SnelheidRechts];
        MotorC[6] = (SnelheidRechts == 0 ? 0 : richtingRechts);

    }
	writeData(&MotorC[0], 7);
}

void MotorInit() {
	uint8_t Totalpower[2]={4,250};
	uint8_t Softstart[3]={0x91,23,0};
	fd = wiringPiI2CSetup(0x32);
	if (fd < 0){
		printf("wiringPiI2CSetup failed.\n");
	}
	writeData(&Totalpower[0], 2);
	
	writeData(&Softstart[0], 3);
	
}