import RPi.GPIO as gpio
import time
import threading
import smbus
import socket
import sys

gpio.setmode(gpio.BCM)

gpio.setup(23, gpio.IN)
gpio.setup(24, gpio.IN)
gpio.setup(20, gpio.OUT)
gpio.output(20,False)
gpio.setup(17, gpio.OUT)
gpio.output(17,False)
gpio.setup(21, gpio.OUT)
gpio.output(21,False)
gpio.setup(6, gpio.OUT) # left_trig
gpio.setup(5, gpio.IN) # left_echo
gpio.setup(26, gpio.OUT) # buzzor
gpio.setup(13, gpio.OUT) # right_trig
gpio.setup(19, gpio.IN) # right_echo
gpio.setup(18, gpio.OUT)
gpio.output(18, False)

HOST = '192.168.1.50'
PORT = 7622

tl_data = ""
tr_data = ""

transfer = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
transfer.connect((HOST,PORT))

power_mgmt_1 = 0x6b

power_mgmt_2 = 0x6c

def read_byte(adr):

    return bus.read_byte_data(address, adr)

def read_word(adr):

    high = bus.read_byte_data(address, adr)

    low = bus.read_byte_data(address, adr+1)

    val = (high << 8) + low

    return val


def read_word_2c(adr):

    val = read_word(adr)

    if (val >= 0x8000):

        return -((65535 - val) + 1)

    else:

        return val



def dist(a,b):

    return math.sqrt((a*a)+(b*b))



def get_y_rotation(x,y,z):

    radians = math.atan2(x, dist(y,z))

    return -math.degrees(radians)


def get_x_rotation(x,y,z):

    radians = math.atan2(y, dist(x,z))

    return math.degrees(radians)


class left_light(threading.Thread):
    def run(self):
        global tl_data
        while True:
            if gpio.input(23) == 0:
                
                gpio.output(6, False)
                time.sleep(0.5)
                gpio.output(6, True)
                time.sleep(0.00001)
                
                gpio.output(6, False)
                
                while gpio.input(5) == 0 :
                    pulse_start = time.time()
                while gpio.input(5) == 1 :
                    pulse_end = time.time()
                    pulse_duration = pulse_end - pulse_start
                    distance = pulse_duration * 17000
                    distance = round(distance, 2)
                    
                if distance < 20: # or distance > 1000:
                    print "left_Distance : ", distance, "cm"
                    gpio.output(26, True)
                    if tl_data != "2":
                        transfer.sendall('{"ctname":"left_light", "con":"2"}')
                        tl_data = "2"
                else:
                    if tl_data != "1":
                        transfer.sendall('{"ctname":"left_light", "con":"1"}')
                        tl_data = "1"
                    
                gpio.output(20,True)
                gpio.output(17,True)
                time.sleep(0.5)
                gpio.output(20,False)
                gpio.output(17,False)
                gpio.output(26,False)
            else:
                if tl_data != "0":
                    transfer.sendall('{"ctname":"left_light", "con":"0"}')
                    tl_data = "0"
                time.sleep(0.3)

                

class right_light(threading.Thread):
    def run(self):
        global tr_data
        while True:
            if gpio.input(24) == 0:
                gpio.output(13, False)
                time.sleep(0.5)
                gpio.output(13, True)
                time.sleep(0.00001)

                gpio.output(13, False)
                
                while gpio.input(19) == 0 :
                    pulse_start = time.time()
                    
                while gpio.input(19) == 1 :
                    pulse_end = time.time()
                    pulse_duration = pulse_end - pulse_start
                    distance = pulse_duration * 17000
                    distance = round(distance, 2)
                    
                if distance < 20: # or distance > 1000:
                    print "right_Distance : ", distance, "cm"
                    gpio.output(26, True)
                    if tr_data != "2":
                        transfer.sendall('{"ctname":"right_light", "con":"2"}')
                        tr_data = "2"
                else:
                    if tr_data != "1":
                        transfer.sendall('{"ctname":"right_light", "con":"1"}')
                        tr_data = "1"
                    
                gpio.output(21,True)
                gpio.output(17,True)
                time.sleep(0.5)
                gpio.output(21,False)
                gpio.output(17,False)
                gpio.output(26,False)
                
            else:
                if tr_data != "0":
                    transfer.sendall('{"ctname":"right_light", "con":"0"}')
                    tr_data = "0"
                time.sleep(0.3)


try:
    light_0 = left_light()
    light_0.start()
    light_1 = right_light()
    light_1.start()
    a = input()
        
except(KeyboardInterrupt):
    gpio.cleanup()

