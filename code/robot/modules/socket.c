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

int threatRunning = 0, socketConnection, userSocket; // only one user can connect at the same time
struct sockaddr_in server;
char GUID[36] = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
int web = 0;
pthread_t socketConnectionThread;	// this is our thread identifier

// TODO: Sometimes the socket server crashes (only when 'Client error' ?), fix

int Base64Encode(const unsigned char* buffer, int length, char** b64text) {
	BIO *bio, *b64;
	BUF_MEM *bufferPtr;
	
	b64 = BIO_new(BIO_f_base64());
	bio = BIO_new(BIO_s_mem());
	bio = BIO_push(b64, bio);
	
	BIO_set_flags(bio, BIO_FLAGS_BASE64_NO_NL);
	BIO_write(bio, buffer, length);
	BIO_flush(bio);
	BIO_get_mem_ptr(bio, &bufferPtr);
	BIO_set_close(bio, BIO_NOCLOSE);
	BIO_free_all(bio);
	
	*b64text=(*bufferPtr).data;
	
	return (0);
}




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
	
	struct timeval timeout;
	timeout.tv_sec = 1;
	timeout.tv_usec = 0;
	
	if (setsockopt (socketConnection, SOL_SOCKET, SO_SNDTIMEO, (char *)&timeout, sizeof(timeout)) < 0)
		printf("setsockopt failed\n");
	
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
	sleep(5);
	//
	//	writeToSocket(6, "180");
	//	writeToSocket(3, "123");
	//	writeToSocket(2, "87");
	//	writeToSocket(7, "12-56 (9,98 euro)");
	//	pthread_join(socketConnectionThread, NULL);
}



void *listenForConnections(void *arg) {
	threatRunning = 1;
	while(1) {
		long read_size;
		struct sockaddr_in client;
		int c, i, authenticated = 0, readSize = 1024, tmpUserSocket;
		char client_message[1024], keystr[43], websocketKey[24], string[sizeof(websocketKey)+sizeof(GUID)+ 1];
		
		unsigned char hash[SHA_DIGEST_LENGTH], encoded[2], key[4];
		char response[130];
		char * websocketKeyLoc, * tokenRaw;
		
		c = sizeof(struct sockaddr_in);
		
		tmpUserSocket = accept(socketConnection, (struct sockaddr *)&client, (socklen_t*)&c);
		if (tmpUserSocket == -1)
			printf("accept failed\n");
		
		if(userSocket >= 0) // if there is already a socket connected, disconnect
			close(userSocket);
		
		userSocket = tmpUserSocket;
		
		//char * sendBuff = "Connection established.";
		//write(userSocket, sendBuff, strlen(sendBuff));
		
        printf("Client has connected!\n");
		while((read_size = recv(userSocket, client_message, readSize, 0)) > 0 ){
			//write(userSocket, client_message, strlen(client_message));
			if (!authenticated) {
				websocketKeyLoc = strstr(client_message, "Sec-WebSocket-Key");
				if (websocketKeyLoc != NULL) {
					strncpy(keystr, websocketKeyLoc, 43);
					strncpy(websocketKey, strstr(keystr, ": ")+2, 24);
					sprintf(string,"%.24s%.36s", websocketKey, GUID);
					
					SHA1((unsigned char *)&string, 60, (unsigned char*)&hash);
					
					Base64Encode(( const unsigned char * )&hash, SHA_DIGEST_LENGTH, &tokenRaw);
					
					sprintf(response,"HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: %.28s\r\n\r\n", tokenRaw);
					write(userSocket, response, sizeof(response)-1);
					authenticated = 1;
					readSize = 9;
					web = 1;
					continue;
				}
			}
			
			if (readSize > 2){
				encoded[0] = client_message[6];
				encoded[1] = client_message[7];
				key[0] = client_message[2];
				key[1] = client_message[3];
				key[2] = client_message[4];
				key[3] = client_message[5];
				for ( i = 0; i < sizeof(encoded); i++) {
					client_message[i] = (unsigned char) (encoded[i] ^ key[i % 4]);
				}
				client_message[2] = '\0';
			}
			
			//write(userSocket, client_message, strlen(client_message));
			uint8_t opcode = client_message[0];
			
			// Remove the opcode from the client_message
			for(i = 0; i < (strlen(client_message) + 1); i++) {
				if(i != 0)
					client_message[i - 1] = client_message[i];
			}
			if(strlen(client_message) > 0){
				client_message[i] = '\0';
			}
			
			if(DEBUG) {
				printf("Opcode: %d\n", opcode);
				printf("Message: %d\n", client_message[0]);
			}
			
			onCommand(opcode, client_message);
			
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
		
		readSize = 1024;
		authenticated = 0;
		
		socketInit();
		onDisconnect();
	}
}

void writeToSocket(uint8_t opcode, char *commandData) {
	if(userSocket > 0) {
		char client_message[1025];
		int i, writeSocket;
		for (i = 0; i < sizeof(client_message); i++) {
			client_message[i] = '\0';
		}
		client_message[0] = opcode;
		if(DEBUG) printf("Write to socket len %lu: ", strlen(commandData));
		for(i = 1; i < 1024; i++){
			
			if(DEBUG) printf("%d", client_message[i-1]);
			
			if (i-1 < strlen(commandData)) {
				client_message[i] = commandData[i-1];
			}
			else{
				client_message[i] = '\0';
			}
		}
		if(DEBUG) printf("\n");
		
		if (web) {
			char frame[131];
			for (i = 0; i < sizeof(frame); i++) {
				frame[i] = '\0';
			}
			
			frame[0] = 129;
			frame[1] = strlen(client_message);
			client_message[0] = client_message[0]+'0';
			snprintf(frame+2, 124, "%s", client_message);
			printf("%s", frame);
			
			writeSocket = (int) write(userSocket, frame, 2 + strlen(client_message));
			
		}else{
			writeSocket = (int) write(userSocket, client_message, 1025);
		}
		
		
		
		if (writeSocket == -1 ) {
			close(socketConnection);
			close(userSocket);
			
			userSocket = -1;
			socketInit();
			onDisconnect();
			
		}
	}
}
