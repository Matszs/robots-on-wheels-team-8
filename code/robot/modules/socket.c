//
//  socket.c
//
//  Created by Mats Otten on 14-09-15.
//  Copyright (c) 2015 Mats Otten. All rights reserved.
//

void onCommand(uint8_t opcode, char *commandData);
void writeToSocket(uint8_t opcode, char *commandData);
void onDisconnect();
void *listenForConnections(void *arg);
void run();
int threatRunning = 0;
struct sockaddr_in server;
int socketConnection;
int userSocket; // only one user can connect at the same time

pthread_t socketConnectionThread;	// this is our thread identifier

// TODO: Sometimes the socket server crashes (only when 'Client error' ?), fix

void socketInit() {
    socketConnection = socket(AF_INET , SOCK_STREAM , 0);

    if (socketConnection == -1)
        printf("Could not create socket");

    //Prepare the sockaddr_in structure
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons( PORT_NUMBER );

    // Make is possible to re-use ports.
    int yes = 1;
    if ( setsockopt(socketConnection, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1 )
        printf("Error so_reusaddr");

    //Bind
    if( bind(socketConnection,(struct sockaddr *)&server , sizeof(server)) == -1)
        printf("Couln't bind to socket, port already in use?");

	printf("\n----------------------- ");
    printf("Socket connection opened, ready to take connections on port %d", PORT_NUMBER);
    printf(" -----------------------\n\n");

    //Listen
    listen(socketConnection , 3);

    //start listening in other thread
	if (!threatRunning){
		pthread_create(&socketConnectionThread, NULL, listenForConnections, NULL);
	}
}

void *listenForConnections(void *arg) {
	threatRunning = 1;
    while(1) {
        int c;
        long read_size;
        struct sockaddr_in client;
        char client_message[1024];

        c = sizeof(struct sockaddr_in);

        int tmpUserSocket;

        tmpUserSocket = accept(socketConnection, (struct sockaddr *)&client, (socklen_t*)&c);
        if (tmpUserSocket == -1)
            printf("accept failed\n");

        if(userSocket >= 0) // if there is already a socket connected, disconnect
            close(userSocket);

        userSocket = tmpUserSocket;

        //char * sendBuff = "Connection established.";
        //write(userSocket, sendBuff, strlen(sendBuff));

        printf("Client has connected!\n");

        while((read_size = recv(userSocket, client_message, 1024, 0)) > 0 ){
        	//write(userSocket, client_message, strlen(client_message));
        	uint8_t opcode = client_message[0];

			// Remove the opcode from the client_message
			int j;
			for(j = 0; j < (strlen(client_message) + 1); j++) {
				if(j != 0)
					client_message[j - 1] = client_message[j];
			}
			if(strlen(client_message) > 0){
				client_message[j] = '\0';
			}

			if(DEBUG) {
				printf("Opcode: %d\n", opcode);
				printf("Message: %d\n", client_message[0]);
			}
            onCommand(opcode, client_message);

            int i;
			for (i = 0; i < read_size; i++) {
				client_message[i] = '\0';
			}
        }

        if(read_size == 0) {
            close(userSocket);
            printf("Client disconnected\n");
        }
        else if(read_size == -1) {
            userSocket = -1;
            printf("Client error\n");
        }
		close(socketConnection);
		socketInit();
        onDisconnect();
    }
}

void writeToSocket(uint8_t opcode, char *commandData) {
    if(userSocket > 0) {
        char client_message[1025];

        client_message[0] = opcode;
		if(DEBUG) {
			printf("Write to socket len %d: ", strlen(commandData));
		}
        int i;
		for(i = 0; i < 1024; i++){
			if(DEBUG) {
				printf("%d", client_message[i]);
			}

			if (i < strlen(commandData)) {
				client_message[i + 1] = commandData[i];
			}
			else{
				client_message[i + 1] = '\0';
			}
		}
		if(DEBUG) {
			printf("\n");
		}
		

        write(userSocket, client_message, 1025);
	}
}