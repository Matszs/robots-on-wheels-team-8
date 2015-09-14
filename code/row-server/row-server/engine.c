#include <stdio.h>
#include <wiringPiI2C.h>
int fd;

void writeData(int * data, int lenght){
	for (int i = 0; i<lenght; i++) {
		wiringPiI2CWrite(fd, data[i]);
		usleep(1);
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
void init(){
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

	stop();
	usleep(10000);
	forward(0xa5);
	usleep(10000);
	stop();
	usleep(10000);
	reverse(0xa5);
	usleep(10000);
	stop();
	
	
}