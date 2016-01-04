//
//  main.c
//  test
//
//  Created by ekko scholtens on 15/12/2015.
//  Copyright © 2015 ekko scholtens. All rights reserved.
//
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <pthread.h>
#include <openssl/sha.h>
#include <openssl/hmac.h>
#include <openssl/evp.h>
#include <openssl/bio.h>
#include <openssl/buffer.h>
#include "socket.c"
int main(){
	socketInit();
	
}
void onDisconnect(){
	
}
void onCommand(uint8_t opcode, char *commandData){
	
}