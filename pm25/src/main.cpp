/*
 * main.cpp
 *
 *  Created on: Oct 10, 2015
 *      Author: Menchi Guo
 *      E-mail: menchi.guo@gmail.com
 */

#include <stdio.h>
#include <string.h>
#include <unistd.h>

#include "DJI_LIB/DJI_Pro_Codec.h"
#include "DJI_LIB/DJI_Pro_Hw.h"
#include "DJI_LIB/DJI_Pro_Link.h"
#include "DJI_LIB/DJI_Pro_App.h"
#include "DJI_LIB/DJI_Pro_Config.h"
#include "DJI_LIB/DJI_Pro_Rmu.h"


int16_t sdk_pure_transfer_hander(uint8_t* pbuf, uint16_t len)    
{                                                                                                                                                                                        
    /* DJI_LIB */
    DJI_Pro_App_Send_Data(0 , 0, MY_ACTIVATION_SET, 0xFE, pbuf, len,NULL,0,1);          

    printf("[pure_transfer],send len %d data %s\n", len, pbuf);                                                                                                                         
}

int init_pm25(const char *device, int baudrate);
int read_pm25(char *buf, int len);
void close_pm25();

char buffer[1024];

int main()
{
    if(Pro_Hw_Setup("/dev/ttyAMA0", 230400) < 0)      /* Open RPi <-> DJI Serial Port */
    {
        perror( "UAV Serial Port Open ERROR" );
        return 0;
    }
    DJI_Pro_Setup(NULL);                              /* Setup DJI SDK */
    
    if(init_pm25("/dev/ttyUSB1", 2400) <0)            /* Open RPi <-> PM25 Serial Port */
    {
        perror( "PM25 Serial Port Open ERROR" );
        return 0;
    }

    /* activation */
    activate_data_t user_act_data; 

    char key_buf[65] = "Input your app_key ";   /* Input your app_key */
    char app_bundle_id[32] = "1234567890";

    user_act_data.app_id = Input your app_id;                     /* Input your app_id */
    user_act_data.app_api_level = Input your app_level;                    /* Input your app_level */
    user_act_data.app_ver = 0x02030A00; 
    user_act_data.app_key = key_buf;  
    strcpy((char*)user_act_data.app_bundle_id, app_bundle_id);

    DJI_Pro_Activate_API(&user_act_data,NULL);



    while(1)
    {
        int nbyte;
        nbyte = read_pm25(buffer, 1024);          
        if (nbyte > 0) 
        {
            sdk_pure_transfer_hander((uint8_t*)buffer, nbyte);   /* Transparent-Transmit */
            printf("%s", buffer);
        } 

        sleep(1);
    }
    close_pm25();
}