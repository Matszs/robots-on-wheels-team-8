//
//  control.c
//  Robot
//
//  Created by Patrick Hendriks on 14-09-15.
//  Copyright (c) 2015 Patrick Hendriks. All rights reserved.
//  Toegestaan om voor het Project Robot on Wheels vrij te gebruiken en of aan te passen.
//  Edited by Ekko Scholtens on 22-09-15.
//  Edited by Mats Otten on 22-09-15.
//

int motor;
int lastSpeedLeft = -1;
int lastSpeedRight = -1;
int lastDirectionLeft = -1;
int lastDirectionRight = -1;
uint8_t speedTable[7] = {0, 4, 8, 12, 16, 20, 24};
int ledLeftPin = 10;
int ledRightPin = 11;
int ledLeftState = 0;
int ledRightState = 0;

typedef struct {
	uint8_t Left:4;
	uint8_t Right:4;
} movement;

void writeData(uint8_t * data, int lenght){
	int i;
	for (i = 0; i < lenght; i++)
		wiringPiI2CWrite(motor, data[i]);
}

void toggleLed(int pin, int * state){
	*state = *state == 0 ? 1 : 0;
	digitalWrite(pin, *state);
}


void MotorInit() {
	uint8_t Totalpower[2]={4,250};
	uint8_t Softstart[3]={0x91,23,0};
	motor = wiringPiI2CSetup(0x32);
	if (motor < 0)
		printf("wiringPiI2CSetup failed.\n");
	writeData(&Totalpower[0], 2);
	writeData(&Softstart[0], 3);
	pinMode(ledLeftPin, OUTPUT);
	pinMode(ledRightPin, OUTPUT);
	ledRightState = 0;
	digitalWrite(ledRightPin, ledRightState);
	ledLeftState = 0;
	digitalWrite(ledLeftPin, ledLeftState);

}

void unpackMovement(uint8_t input, movement *direction){
    direction->Left = input >> 4;
    direction->Right = (input << 4) >> 4;
}

void MotorcontrolMovement(movement *direction){
	
	uint8_t richtingLinks = ((direction->Left >> 3 & 1) == 1) ? 1 : 2;
	uint8_t richtingRechts = ((direction->Right >> 3 & 1) == 1) ? 1 : 2;
	uint8_t rotationSpeedLeft = direction->Left & 7;
	uint8_t rotationSpeedRight = direction->Right & 7;
	
	if(DEBUG) {
		printf("direction->Left:        %d\n", direction->Left);
		printf("direction->Right:       %d\n", direction->Right);
		printf("rotationDirectionLeft:  %d\n", richtingLinks);
		printf("rotationDirectionRight: %d\n", richtingRechts);
		printf("rotationSpeedLeft:      %d\n", rotationSpeedLeft);
		printf("rotationSpeedRight:     %d\n\n", rotationSpeedRight);
	}
	

    uint8_t MotorC[7];

    // No speed, no direction so stop moving.
    if(richtingLinks == 2 && rotationSpeedLeft == 0 && richtingRechts == 2 && rotationSpeedRight == 0) {
        if(DEBUG)
            printf("STOP!\n");
        MotorC[0] = 7;
        MotorC[1] = 0;
        MotorC[2] = 0;
        MotorC[3] = 0;
        MotorC[4] = 0;
        MotorC[5] = 0;
        MotorC[6] = 0;
        isDriving = 0;

        printf("Engine: STOP \n");
        writeData(&MotorC[0], 7);
		ledRightState = 0;
		digitalWrite(ledRightPin, ledRightState);
		ledLeftState = 0;
		digitalWrite(ledLeftPin, ledLeftState);

    } else {
        printf("STOP: %d \n", hasToStop);

        if(automaticStop && hasToStop && (richtingLinks == 2 || richtingRechts == 2)) {
            printf("No forward\n");
            if(isDriving == 1)
                MotorInit(); // reset engine because sometimes it crashes????
            isDriving = 0;
        } else {
            //if(DEBUG)
            if(lastSpeedLeft != speedTable[rotationSpeedLeft] || lastSpeedRight != speedTable[rotationSpeedRight] || lastDirectionLeft != (rotationSpeedLeft == 0 ? 0 : richtingLinks) || lastDirectionRight != (rotationSpeedRight == 0 ? 0 : richtingRechts)) {

                printf("lft; %d, rgh; %d\n", speedTable[rotationSpeedLeft], speedTable[rotationSpeedRight]);

                MotorC[0] = 7;
                MotorC[1] = 3;
                MotorC[2] = lastSpeedLeft = speedTable[rotationSpeedLeft];
                MotorC[3] = lastDirectionLeft = (rotationSpeedLeft == 0 ? 0 : richtingLinks);
                MotorC[4] = 3;
                MotorC[5] = lastSpeedRight = speedTable[rotationSpeedRight];
                MotorC[6] = lastDirectionRight = (rotationSpeedRight == 0 ? 0 : richtingRechts);
                isDriving = 1;

				if(lastSpeedLeft > lastSpeedRight){
					printf("right");
					ledLeftState = 0;
					digitalWrite(ledLeftPin, ledLeftState);
					toggleLed(ledRightPin, &ledRightState);
				}else if(lastSpeedLeft < lastSpeedRight){
					printf("left");
					ledRightState = 0;
					digitalWrite(ledRightPin, ledRightState);
					toggleLed(ledLeftPin, &ledLeftState);

				}else{
					ledRightState = 0;
					digitalWrite(ledRightPin, ledRightState);
					ledLeftState = 0;
					digitalWrite(ledLeftPin, ledLeftState);

				}
                printf("Engine: riding \n");
                writeData(&MotorC[0], 7);
            }
        }
    }
}

