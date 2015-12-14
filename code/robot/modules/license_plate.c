//
//  license_plate.c
//
//  Created by Mats Otten on 6-12-15.
//  Copyright (c) 2015 Mats Otten. All rights reserved.
//

#include  <stdio.h>
#include  <unistd.h>
#include  <time.h>
#include <my_global.h>
#include <mysql.h>
#include <string.h>
#include <stdlib.h>

MYSQL * conn;
MYSQL_RES *res = NULL;
char * mysqlServer = "akoo.nl";
char * mysqlUser = "row";
char * mysqlPassword = "row";
char * mysqlDatabase = "row";

void *licensePlateHandler(void *arg);
char *getFineByLicense(char license[100]);

void licensePlateInit() {
	conn = mysql_init(NULL);

   if (!mysql_real_connect(conn, mysqlServer, mysqlUser, mysqlPassword, mysqlDatabase, 0, NULL, 0)) {
	  fprintf(stderr, "%s\n", mysql_error(conn));
	  exit(1);
   }
}

void licensePlateReader() {
    pthread_t licensePlateThread;
    pthread_create(&licensePlateThread, NULL, licensePlateHandler, NULL);
}

void *licensePlateHandler(void *arg) {
    //system("cd /home/pi/cam && fswebcam -r 1920x1080 -S 20 --no-banner --quiet alpr.jpg && tesseract -psm 7 alpr.jpg stdout");

    FILE *ls = popen("cd /home/pi/cam && wget http://localhost:8080?action=snapshot -O alpr.jpg > /dev/null 2>&1 && tesseract -psm 7 alpr.jpg stdout", "r");
    char license[100];
    while (fgets(license, sizeof(license), ls) != 0) {
    	if(strlen(license) > 1) { // fix 'space'
    		strtok(license, "\n");
    		char * fine = getFineByLicense(license);
    		if(fine != NULL) {
    			sprintf(license, "%s (%s euro)", license, fine);
    			printf("Sent: %s\n", license);
				writeToSocket(OPT_LICENSE, &license[0]);
			}
        }
    }

    pclose(ls);
}


void finish_with_error(MYSQL *con) {
    fprintf(stderr, "%sn", mysql_error(con));
    mysql_close(con);
    exit(1);
}

char * getFineByLicense(char license[100]) {
   char query[300];
   sprintf(query,"SELECT fine FROM cars WHERE license LIKE '%%%s%%'", license);

	if(res != NULL)
		mysql_free_result(res);

	if (mysql_query(conn, query)) {
		fprintf(stderr, "%s\n", mysql_error(conn));
	} else {
		res = mysql_use_result(conn);
		MYSQL_ROW row = mysql_fetch_row(res);
		if(row != NULL)
			return row[0];
	}

	return NULL;
}