CC=gcc
CCFLAGS=


LD_LIBS=-lwiringPi -lwiringPiDev -lpthread -lcrypto -lssl -lm -L/usr/lib/arm-linux-gnueabihf -lmysqlclient -lz -lrt -ldl
LD_FLAGS=-L/usr/local/lib -I/usr/include/mysql -DBIG_JOINS=1  -fno-strict-aliasing   -g


OUTDIR=./build
TARGET=robot
SRC=robot

all:
	mkdir -p $(OUTDIR)
	$(CC) -o $(OUTDIR)/$(TARGET) project.c $(CCFLAGS) $(LD_FLAGS) $(LD_LIBS)
