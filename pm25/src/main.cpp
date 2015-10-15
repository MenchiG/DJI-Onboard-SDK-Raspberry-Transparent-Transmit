/*
 * main.cpp
 *
 *  Created on: Oct 10, 2015
 *      Author: Menchi Guo
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

int init_pm25(const char *device, int baudrate);
int read_pm25(char *buf, int len);
void close_pm25();

char buffer[1024];

bool run_flag = false;




void transparent_transission_send(uint8_t* pbuf, uint16_t len)
{    //透传发送！！！
    DJI_Pro_App_Send_Data(0 , 0, MY_ACTIVATION_SET, 0xFE, pbuf, len,NULL,0,1);                                        
    //printf("[send_data],send len %d data %s\n", len, pbuf);
}


void cb_fun (unsigned short result)
{
    char result_tmp[10];
    sprintf(result_tmp, "%d", result);
    transparent_transission_send((uint8_t*)&result_tmp, 4);
}


activate_data_t user_act_data; 
void activation()
{

    char key_buf[65] = "input your key";   /* Input your app_key */
    char app_bundle_id[32] = "1234567890";

    user_act_data.app_id = id;                     /* Input your app_id */
    user_act_data.app_api_level = level;                    /* Input your app_level */
    user_act_data.app_ver = 0x02030A00; 
    user_act_data.app_key = key_buf;  
    strcpy((char*)user_act_data.app_bundle_id, app_bundle_id);

    DJI_Pro_Activate_API(&user_act_data,cb_fun);
}



void transparent_transission_receive(unsigned char *buf,unsigned char len)
{
    unsigned char cmd;
    cmd = buf[0];

   // printf("[recv_data],send len %d data %s\n", len, buf);

    switch(cmd)
    {
        case 'a':   
            activation();
            break;
        case 'b':
            run_flag = true;
            break;
        case 'c':
            run_flag = false;
            break;
        default:
            break;
    }
}



int main()
{
    if(Pro_Hw_Setup("/dev/ttyAMA0", 230400) < 0)
    {
        perror( "UAV Serial Port Open ERROR" );
        return 0;
    }
    DJI_Pro_Setup(NULL);
    
    if(init_pm25("/dev/ttyUSB0", 2400) <0)
    {
        perror( "PM25 Serial Port Open ERROR" );
        return 0;
    }

    DJI_Pro_Register_Transparent_Transmission_Callback(transparent_transission_receive);


    
    while(1)
    {


        if(run_flag)
        {
            int nbyte;
            nbyte = read_pm25(buffer, 1024);
            if (nbyte > 0) 
            {
                transparent_transission_send((uint8_t*)buffer, nbyte);
                //printf("%s", buffer);
            } 
        }

        sleep(1);

    }
    close_pm25();
}
