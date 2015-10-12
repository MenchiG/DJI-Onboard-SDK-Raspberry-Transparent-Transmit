TARGET = ../output/pm25

OBJECTS = main.o DJI_Pro_App.o DJI_Pro_Hw.o DJI_Pro_Link.o DJI_Pro_Codec.o DJI_Pro_Rmu.o pm25.o

CFLAGS = -Wall -O3 -DPLATFORM_LINUX -DTINYXML_CFG -I../src/ -I../src/DJI_LIB

LDFLAGS = -lpthread

CXX = g++

$(TARGET) : $(OBJECTS)
	$(CXX) -o $(TARGET) $(OBJECTS) $(LDFLAGS)
	
main.o : ../src/main.cpp
	$(CXX) $(CFLAGS) -c ../src/main.cpp

pm25.o : ../src/pm25.cpp
	$(CXX) $(CFLAGS) -c ../src/pm25.cpp

DJI_Pro_App.o : ../src/DJI_LIB/DJI_Pro_App.cpp
	$(CXX) $(CFLAGS) -c ../src/DJI_LIB/DJI_Pro_App.cpp
	
DJI_Pro_Hw.o : ../src/DJI_LIB/DJI_Pro_Hw.cpp
	$(CXX) $(CFLAGS) -c ../src/DJI_LIB/DJI_Pro_Hw.cpp

DJI_Pro_Link.o : ../src/DJI_LIB/DJI_Pro_Link.cpp
	$(CXX) $(CFLAGS) -c ../src/DJI_LIB/DJI_Pro_Link.cpp
	
DJI_Pro_Codec.o : ../src/DJI_LIB/DJI_Pro_Codec.cpp
	$(CXX) $(CFLAGS) -c ../src/DJI_LIB/DJI_Pro_Codec.cpp

DJI_Pro_Rmu.o: ../src/DJI_LIB/DJI_Pro_Rmu.cpp
	$(CXX) $(CFLAGS) -c ../src/DJI_LIB/DJI_Pro_Rmu.cpp

	
clean:
	-rm *.o
	-rm $(TARGET)
