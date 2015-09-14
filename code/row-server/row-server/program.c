//
//  main.c
//  row-server
//
//  Created by Mats Otten on 14-09-15.
//  Copyright (c) 2015 Mats Otten. All rights reserved.
//

#define PORT_NUMBER	1212

#include <stdio.h>
#include<sys/socket.h>
#include<arpa/inet.h> //inet_addr

#include "engine.c"

void init();

struct sockaddr_in server;
int socketConnection;

int main() {
//    init();
	testEngine();
    return 0;
}

void init() {
    int new_socket , c;
    struct sockaddr_in client;
    
    socketConnection = socket(AF_INET , SOCK_STREAM , 0);
    
    if (socketConnection == -1) {
        printf("Could not create socket");
    }
    
    //Prepare the sockaddr_in structure
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons( PORT_NUMBER );
    
    //Bind
    if( bind(socketConnection,(struct sockaddr *)&server , sizeof(server)) == -1)
        printf("Couln't bind to socket, port already in use?");
    
    //Listen
    listen(socketConnection , 3);
    
    printf("Waiting for incoming connections...");
    c = sizeof(struct sockaddr_in);
    new_socket = accept(socketConnection, (struct sockaddr *)&client, (socklen_t*)&c);
    if (new_socket<0)
    {
        perror("accept failed");
    }
    
    puts("Connection accepted");
    
}