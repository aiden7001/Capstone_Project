import RPi.GPIO as gpio
import time
import threading
import socket
import sys

gpio.setmode(gpio.BCM)

gpio.setup(23, gpio.IN)
gpio.setup(24, gpio.IN)
gpio.setup(20, gpio.OUT)
gpio.output(20,False)
gpio.setup(21, gpio.OUT)
gpio.output(21,False)
gpio.setup(6, gpio.OUT) # left_trig
gpio.setup(5, gpio.IN) # left_echo
gpio.setup(26, gpio.OUT) # buzzor
gpio.setup(13, gpio.OUT) # right_trig
gpio.setup(19, gpio.IN) # right_echo

HOST = '192.168.1.2'
PORT = 7622

info = ""
transferred_info = ""

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST,PORT))

s.sendall('{"ctname":"parking_mode", "con":""}')

# Update the information of parking_mode in real time.
class ModeCheck(threading.Thread):
        def run(self):
                global mode_data
                global info
                while True:
                        data = s.recv(1024)
                        check = len(data) - 2
                        info = int(data[32:check])
                        print "--------> [parking_mode] : " + str(info)



class left_light(threading.Thread):
    def run(self):
        global info
        while True:
            if True: #info == 1:
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
                        
                    if distance < 20 or distance > 1000:
                        print "left_Distance : ", distance, "cm"
                        gpio.output(26, True)
                        
                    gpio.output(20,True)
                    time.sleep(0.5)
                    gpio.output(20,False)
                    gpio.output(26,False)
                else:
                    time.sleep(0.3)
            else:
                time.sleep(3)

                

class right_light(threading.Thread):
    def run(self):
        global info
        while True:
            if True: #info == 1:
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
                        
                    if distance < 20 or distance > 1000:
                        print "right_Distance : ", distance, "cm"
                        gpio.output(26, True)
                    
                    gpio.output(21,True)
                    time.sleep(0.5)
                    gpio.output(21,False)
                    gpio.output(26,False)
                    
                else:
                    time.sleep(0.3)
                    
            else:
                time.sleep(3)

try:
    #mode_check = ModeCheck()
    #mode_check.start()
    light_0 = left_light()
    light_0.start()
    light_1 = right_light()
    light_1.start()
    a = input()
except(KeyboardInterrupt):
    gpio.cleanup()
    

