#include <stdio.h>
#include <wiringPiI2C.h>
#include <unistd.h>
#include <time.h>

int fd;

void writeData(int * data, int lenght){
	for (int i = 0; i < lenght; i++) {
		wiringPiI2CWrite(fd, data[i]);
	}
}

void forward(int speed){
	int forward[7] = {7 ,3 ,speed, 2 ,3 ,speed ,2};
	writeData(&forward[0], 7);
}
void reverse(int speed){
	int reverse[7] = {7 ,3 ,speed, 1 ,3 ,speed ,1};
	writeData(&reverse[0], 7);
}
void stop(){
	int stop[7] = {7 ,0 ,0, 0 ,0 ,0 ,0};
	writeData(&stop[0], 7);
}
void left(int speed){
	int left[7] = {7 ,3 ,speed, 1 ,3 ,speed ,2};
	writeData(&left[0], 7);
}
void right(int speed){
	int right[7] = {7 ,3 ,speed, 2 ,3 ,speed ,1};
	writeData(&right[0], 7);
}
void turnForward(int speedleft, int speedright){
	int turn[7] = {7 ,3 ,speedleft, 2 ,3 ,speedright ,2};
	writeData(&turn[0], 7);
}
void initEngine(){
	int Totalpower[2]={4,250};
	int Softstart[3]={0x91,23,0};
	fd = wiringPiI2CSetup(0x32);
	if (fd < 0){
		printf("wiringPiI2CSetup failed.\n");
	}
	writeData(&Totalpower[0], 2);
	
	writeData(&Softstart[0], 3);
	
}

void testEngine(){
	initEngine();
	printf("stop.\n");
	stop();
	sleep(1);
	printf("forward.\n");
	forward(0xa5);
	sleep(1);
	printf("stop.\n");
	stop();
	sleep(1);
	printf("reverse.\n");
	reverse(0xa5);
	sleep(1);
	printf("stop.\n");
	stop();
	sleep(1);
	printf("left.\n");
	left(0xa5);
	sleep(1);
	printf("stop.\n");
	stop();
	sleep(1);
	printf("right.\n");
	right(0xa5);
	sleep(1);
	printf("stop.\n");
	stop();
	
	
	
}