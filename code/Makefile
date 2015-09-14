CC=gcc
CCFLAGS=-std=gnu99 -Wall -Werror -g -I/usr/local/include


LD_LIBS= -lwiringPi -lpthread
LD_FLAGS=-L/usr/local/lib


OUTDIR=./build
TARGET=robot

all:  $(TARGET) 

$(TARGET):    $(TARGET).c 
	$(CC) $(CCFLAGS) $(LD_FLAGS) $(LD_LIBS) -o $(OUTDIR)/$(TARGET)