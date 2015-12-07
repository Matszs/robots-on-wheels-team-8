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


void *licensePlateHandler(void *arg);
void check_database(char license[100]);

void licensePlateReader() {
	pthread_t licensePlateThread;
	pthread_create(&licensePlateThread, NULL, licensePlateHandler, NULL);
}

void *licensePlateHandler(void *arg) {
    //system("cd /home/pi/cam && fswebcam -r 1920x1080 -S 20 --no-banner --quiet alpr.jpg && tesseract -psm 7 alpr.jpg stdout");

	FILE *ls = popen("cd /home/pi/cam && wget http://localhost:8080?action=snapshot -O alpr.jpg > /dev/null 2>&1 && tesseract -psm 7 alpr.jpg stdout", "r");
	char buf[100];
	while (fgets(buf, sizeof(buf), ls) != 0) {
		printf("%s", buf);
		check_database(buf);
		writeToSocket(OPT_LICENSE, &buf[0]);
	}

	pclose(ls);
}


void finish_with_error(MYSQL *con) {
	fprintf(stderr, "%s\n", mysql_error(con));
	mysql_close(con);
	exit(1);
}

void check_database(char license[100]) {
    char query[300];

    sprintf(query,"SELECT fine FROM cars WHERE license LIKE '%%s%'",license);
    MYSQL *con = mysql_init(NULL);

    if (con == NULL) {
        fprintf(stderr, "%s\n", mysql_error(con));
        exit(1);
    }

	if (mysql_real_connect(con, "akoo.nl", "row", "row", "row", 0, NULL, 0) == NULL) {
	  finish_with_error(con);
	}

	if (mysql_query(con, query)) {
		printf("Query failed: %s\n", mysql_error(con));
	} else {
		MYSQL_RES *result = mysql_store_result(con);

		if (!result) {
			printf("Couldn't get results set: %s\n", mysql_error(con));
		}

		int num_fields = mysql_num_fields(result);

		MYSQL_ROW row;
		while ((row = mysql_fetch_row(result))) {
			int i;
			for(i = 0; i < num_fields; i++) {
				printf("%s ", row[i] ? row[i] : "NULL");
			}
			printf("\n");
		}
	}

	mysql_close(con);
	return EXIT_SUCCESS;
}
