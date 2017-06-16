import RPi.GPIO as gpio
import time
import threading

gpio.setmode(gpio.BCM)

gpio.setup(23, gpio.IN)
gpio.setup(24, gpio.IN)
gpio.setup(20, gpio.OUT)
gpio.output(20,False)
gpio.setup(21, gpio.OUT)
gpio.output(21,False)

class light_20(threading.Thread):
    def run(self):
        while True:
            if gpio.input(23) == 0:
                time.sleep(0.5)
                gpio.output(20,True)
                time.sleep(0.5)
                gpio.output(20,False)
            else:
                time.sleep(0.3)

class light_21(threading.Thread):
    def run(self):
        while True:
            if gpio.input(24) == 0:
                time.sleep(0.5)
                gpio.output(21,True)
                time.sleep(0.5)
                gpio.output(21,False)
            else:
                time.sleep(0.3)
            
try:
    light_0 = light_20()
    light_0.start()
    light_1 = light_21()
    light_1.start()
    a = input()
        
except(KeyboardInterrupt):
    gpio.cleanup()

