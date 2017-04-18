import blescan
import sys
import socket
import time
import threading

import bluetooth._bluetooth as bluez

dev_id = 0
info = ""

HOST = '192.168.1.2'
PORT = 7622

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST,PORT))
s_1 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s_1.connect((HOST,PORT))

s.sendall('{"ctname":"parking_mode", "con":""}')

try:
	sock = bluez.hci_open_dev(dev_id)
	print "ble thread started"

except:
	print "error accessing bluetooth device..."
    	sys.exit(1)

blescan.hci_le_set_scan_parameters(sock)
blescan.hci_enable_le_scan(sock)

#bea[1] = UUID
#bea[2] = major_num of beacon
#bea[5] = RSSI of beacon
#bea[4] = Tx value of beacon

# 1st condition => there is no beacon
# 2nd condition => beacon is lost
# 3rd condition => beacon distance is differed
# 4th condition => beacon is connected
# 5th condition => another beacon is catched


class ModeCheck(threading.Thread):
        def run(self):
                global info
                while True:
                        data = s.recv(1024)
                        check = len(data) - 2
                        info = int(data[32:check])
                        print "--------> [parking_mode] : " + str(info)

class ConditionCheck(threading.Thread):
        def run(self):
                f_distance
                f_major
                l_distance


class BeaconFinder(threading.Thread):
        def run(self):
                global info
                
                while True:
                        if info == 1: # activate the beacon
                                returnList = blescan.parse_events(sock, 10)
                                sum_1 = 0.0
                                num_1 = 0
                                sum_2 = 0.0
                                num_2 = 0
                                TX_val = 0
                                location_data = -1
                                
                                print "-----[beacon finder]-----"
                                for beacon in returnList:
                                        bea = beacon.split(',')
                                        if bea[1] == "20cae8a0a9cf11e3a5e20800200c9a66":
                                                if bea[2] == "39":
                                                        sum_1 = sum_1 + float(bea[5])
                                                        num_1 = num_1 + 1
                                                        TX_val = float(bea[4])
                                                                
                                                elif bea[2] == "234":
                                                        sum_2 = sum_2 + float(bea[5])
                                                        num_2 = num_2 + 1
                                                        TX_val = float(bea[4])
                                if info == 0:
                                        continue
                                
                                if num_1 != 0:
                                        RSSI_1 = sum_1 / num_1
                                        distance_1 = pow(10, ((TX_val - RSSI_1)/20.0))
                                        print "major = 39, distance = " + str(distance_1) + ", checked_num = " + str(num_1)
                                                
                                if num_2 != 0:
                                        RSSI_2 = sum_2 / num_2
                                        distance_2 = pow(10, ((TX_val - RSSI_2)/20.0))
                                        print "major = 234, distance = " + str(distance_2) + ", checked_num = " + str(num_2)

                                if distance_1 < distance_2:
                                        print "39 beacon is closer"
                                        if location_data != 39:
                                                s_1.sendall('{"ctname":"parking_location", "con":"39"}')
                                                location_data = 39
                                        
                                elif distance_2 < distance_1:
                                        print "234 beacon is closer"
                                        if location_data != 234:
                                                s_1.sendall('{"ctname":"parking_location", "con":"234"}')
                                                location_data = 234

                                
                                        
                        else: # deactivate the beacon
                                time.sleep(1)
                                

mode_check = ModeCheck()
mode_check.start()
beacon_finder = BeaconFinder()
beacon_finder.start()



# (DB) parking_location => location of the bicycle(beacon major number)
# (DB) parking_mode => parking mode(1) or driving mode(0) => check the data and actuate the beacon
# (DB) parking_state => "stolen", "connbeacon", "safe", "nobeacon" 
