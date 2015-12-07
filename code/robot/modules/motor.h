//
//  control.h
//  Robot
//
//  Created by Patrick Hendriks on 14-09-15.
//  Copyright (c) 2015 Patrick Hendriks. All rights reserved.
//

#ifndef __Robot__control__
#define __Robot__control__

#include <stdio.h>

//uint8_t speedTable[7] = {0x0,0x46,0x59,0x6c,0x7f,0x92,0xa5};
uint8_t speedTable[7] = {0, 4, 8, 12, 16, 20, 24};

typedef struct {
    uint8_t Left:4;
    uint8_t Right:4;
} movement;

void unpackMovement(uint8_t getal, movement *direction);

void MotorcontrolMovement(movement *direction);

void MotorInit();


#endif /* defined(__Robot__control__) */
