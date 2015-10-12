/*
 * pm25.cpp
 *
 *  Created on: Oct 10, 2015
 *      Author: Menchi Guo
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <termios.h>
#include <errno.h>

int pm25_fd;
int init_pm25(const char *device, int baudrate)
{
    pm25_fd = open(device, O_RDWR | O_NOCTTY);
    if(pm25_fd  < 0)
    {
        perror("Open PM25 Failed\n");
        return -1;
    }


    struct termios Opt;

    if(tcgetattr(pm25_fd, &Opt) != 0 )
    {
        perror("tcgetattr Failed\n");
        return -1;
    }

    tcflush(pm25_fd, TCIOFLUSH);

    Opt.c_cflag |= (CLOCAL | CREAD);

    Opt.c_cflag &= ~CSIZE;
    Opt.c_cflag |= CS8;

    Opt.c_cflag &= ~PARENB;
    Opt.c_cflag &= ~INPCK;

    Opt.c_cflag &= ~CSTOPB;

    cfsetispeed(&Opt, B2400);
    cfsetospeed(&Opt, B2400);

    Opt.c_lflag  &= ~(ICANON | ECHO | ECHOE | ISIG);
    Opt.c_oflag  &= ~OPOST;

    Opt.c_cc[VTIME]  = 0;
    Opt.c_cc[VMIN] = 0;

    tcflush(pm25_fd, TCIOFLUSH);

    if (tcsetattr(pm25_fd, TCSANOW, &Opt) != 0 )
    {
        perror("tcsetattr Failed\n");
        return -1;
    }

    return 0;
    
}

int read_pm25(char *buf, int len)
{
    int ret = -1;
    ret = read(pm25_fd, buf, len);
    return ret;
}

void close_pm25()
{
    close(pm25_fd);
}