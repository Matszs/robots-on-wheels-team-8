#include <stdio.h>
#include <wiringPiI2C.h>
int fd;

void forward(int speed){
	uint8_t forward[7] = {7 ,3 ,speed, 2 ,3 ,speed ,2};
	wiringPiI2CWrite(fd, forward);
}
void reverse(int speed){
	uint8_t reverse[7] = {7 ,3 ,speed, 1 ,3 ,speed ,1};
	wiringPiI2CWrite(fd, reverse);
}
void stop(){
	uint8_t stop[7] = {7 ,0 ,0, 0 ,0 ,0 ,0};
	wiringPiI2CWrite(fd, stop);
}
void init(){
	uint8_t Totalpower[2]={4,250};
	uint8_t Softstart[3]={0x91,23,0};
	fd = wiringPiI2CSetup(0x32);
	if (fd < 0){
		printf("wiringPiI2CSetup failed.\n");
	}
	if (!wiringPiI2CWrite(fd, Totalpower)){
		printf("gpioI2cWriteData failed\n");
	}
	
	wiringPiI2CSetup(fd, Softstart);

}