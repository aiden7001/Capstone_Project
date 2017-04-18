import blescan
import sys
import socket
import time
import threading

import bluetooth._bluetooth as bluez

dev_id = 0
info = ""
a_distance = 0.0
b_distance = 0.0
stop_check = 0.0

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

class BeaconCondition(threading.Thread):
        def run(self):
                global stop_check
                l_check = ""
                count = 0
                transferred_info = ""
                while True:
                        if info == 1:
                                time.sleep(3)
                                if stop_check == l_check:
                                        count = count+1
                                else:
                                        count = 0
                                l_check = stop_check
                                if count >= 3:
                                        print "no beacon"
                                
                        else:
                                time.sleep(5)


#bea[1] = UUID
#bea[2] = major_num of beacon
#bea[5] = RSSI of beacon
#bea[4] = Tx value of beacon
class BeaconFinder(threading.Thread):
        def run(self):
                global info
                global a_distance
                global b_distance
                global stop_check
                
                location_data = -1
                
                while True:
                        if info == 1: # activate the beacon
                                sum_1 = 0.0
                                num_1 = 0
                                sum_2 = 0.0
                                num_2 = 0
                                TX_val = 0
                                distance_1 = 100.0
                                distance_2 = 100.0
                                print "-----[beacon finder]-----"
                                while num_1 <= 4 and num_2 <= 4:
                                        returnList = blescan.parse_events(sock, 2)
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
                                        stop_check = bea[5]

                                        
                                if info == 0:
                                        continue
                                
                                if num_1 != 0:
                                        RSSI_1 = sum_1 / num_1
                                        distance_1 = pow(10, ((TX_val - RSSI_1)/20.0))
                                        a_distance = distance_1
                                        print "major = 39, distance = " + str(distance_1) + ", checked_num = " + str(num_1)
                                                
                                if num_2 != 0:
                                        RSSI_2 = sum_2 / num_2
                                        distance_2 = pow(10, ((TX_val - RSSI_2)/20.0))
                                        b_distance = distance_2
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

beacon_condition = BeaconCondition()
beacon_condition.start()



# (DB) parking_location => location of the bicycle(beacon major number)
# (DB) parking_mode => parking mode(1) or driving mode(0) => check the data and actuate the beacon
# (DB) parking_state => "stolen", "connbeacon", "safe", "nobeacon" 
