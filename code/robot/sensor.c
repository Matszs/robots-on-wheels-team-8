// sudo gcc -o sensor sensor.c -L/usr/local/lib  -lwiringPi -lwiringPiDev -lpthread -lm -lrpigpio

#include  <stdio.h>
#include  <unistd.h>
#include  "rpiGpio.h"
#include  <wiringPi.h>
#include  <softPwm.h>

int main() {
    setvbuf(stdout, NULL, _IONBF, 0); // display printf's

	uint8_t TxData1[2] = {00};
    //add second array element; read the datasheet of the srf02
    uint8_t TxData2[1] = {0x03};
    //What is the function of memory location 3?;
    //see datasheet srf02
    uint8_t low=50;



    if (gpioSetup()!= OK)
           dbgPrint(DBG_INFO, "gpioSetup failed.\n");


    else if (gpioI2cSetup() != OK)
            dbgPrint(DBG_INFO, "gpioI2cSetup failed.\n");


    if (gpioI2cSet7BitSlave(0x70) != OK)
            dbgPrint(DBG_INFO, "gpioI2cSet7BitSlave failed.\n");


    //set servo in mid position
    //servo pwm control wire must me connected to wiring pi GPIO number 1
    wiringPiSetup();
    pinMode(1,output);
    digitalWrite(1,LOW);
    pwmSetClock(500);  //add  explanation

    softPwmCreate(1,0,500); //add explanation
    softPwmWrite(1,19); //add explination

    usleep(1000);

    while(1)
    {
       // start a new measurement in centimeters
       gpioI2cWriteData(&TxData1[0],2);
       usleep(100000);  //give the sensor time for measurement

       gpioI2cWriteData(&TxData2[0],1);
       //ask for the lower order byte of the range
       gpioI2cReadData(&low,1);

       printf("Distance is %d   \n",low);
       usleep(2000000);

     }


    gpioI2cCleanup();
    gpioCleanup();


    return 0;
}