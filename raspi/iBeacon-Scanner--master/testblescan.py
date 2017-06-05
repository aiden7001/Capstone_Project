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

# information which is transferred to database
transferred_info = ""


HOST = '192.168.43.76'
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

# Update the information of parking_mode in real time.
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
                global transferred_info
                stop_check = l_check
                while True:
                        # if parking_mode information is 1
                        if info == 1:
                                print "info == 1"
                                time.sleep(3)
                                if stop_check == l_check:
                                        count = count+1
                                else:
                                        count = 0
                                
                                if count >= 3:
                                        if transferred_info != "nobeacon":
                                                s_1.sendall('{"ctname":"parking_state", "con":"nobeacon"}')
                                                transferred_info = "nobeacon"
                                                print "no beacon"
                                l_check = stop_check
                        # if parking_mode information is 0
                        else:
                                print "info == 0"
                                if transferred_info != "deactivate":
                                       s_1.sendall('{"ctname":"parking_state", "con":"deactivate"}')
                                       transferred_info = "deactivate"
                                       print "deactivate"
                                time.sleep(3)


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
                global transferred_info
                f1_distance = 0.0
                f2_distance = 0.0
                location_data = -1
                count_1 = 0
                beacon_1_lost = 0
                beacon_2_lost = 0

                
                while True:
                        # if parking_mode information is 1
                        if info == 1:
                                # activate the beacon
                                sum_1 = 0.0
                                num_1 = 0
                                sum_2 = 0.0
                                num_2 = 0
                                TX_val = 0
                                distance_1 = 100.0
                                distance_2 = 100.0

                                
                                print "-----[beacon finder]-----"
                                while num_1 < 10 and num_2 < 10: # change here
                                        returnList = blescan.parse_events(sock, 10) # change here
                                        for beacon in returnList:
                                                bea = beacon.split(',')
                                                if bea[2] == "39" or bea[2] == "234":
                                                        if bea[2] == "39":
                                                                # find average value
                                                                sum_1 = sum_1 + float(bea[5])
                                                                num_1 = num_1 + 1
                                                                TX_val = float(bea[4])
                                                                        
                                                        elif bea[2] == "234":
                                                                sum_2 = sum_2 + float(bea[5])
                                                                num_2 = num_2 + 1
                                                                TX_val = float(bea[4])

                                                        # if there is beacon stop_check value is changed periodically
                                                        stop_check = bea[5]

                                # info value changes to 0 from 1
                                if info == 0:
                                        time.sleep(2)
                                        continue

                                # if bea[2] == "39" data is exist
                                if num_1 != 0:
                                        RSSI_1 = sum_1 / num_1
                                        distance_1 = pow(10, ((TX_val - RSSI_1)/20.0))
                                        a_distance = distance_1
                                        print "major = 39, distance = " + str(distance_1) + ", checked_num = " + str(num_1)
                                
                                # if bea[2] == "234" data is exist         
                                if num_2 != 0:
                                        RSSI_2 = sum_2 / num_2
                                        distance_2 = pow(10, ((TX_val - RSSI_2)/20.0))
                                        b_distance = distance_2
                                        print "major = 234, distance = " + str(distance_2) + ", checked_num = " + str(num_2)

                                # if count_1 is 0 you have to check which beacon is closer and after this process, change the count_1 value to 1
                                if count_1 == 0:
                                        if distance_1 < distance_2:
                                                f2_distance = 100.0
                                        else:
                                                f1_distance = 100.0
                                        count_1 = count_1 + 1
                                
                                if f2_distance == 100.0:
                                        print "39 beacon"
                                        if f1_distance == 0.0:
                                                print "39 beacon is connected"
                                                if transferred_info != "safe":
                                                        s_1.sendall('{"ctname":"parking_state", "con":"safe"}')
                                                        transferred_info = "safe"
                                                        print "safe" 
                                                f1_distance = distance_1
                                                f2_distance = 100.0
                                                
                                        else:
                                                if abs(distance_1 - f1_distance) > 0.5:
                                                        if beacon_1_lost > 1:
                                                                if transferred_info != "stolen":
                                                                        s_1.sendall('{"ctname":"parking_state", "con":"stolen"}')
                                                                        transferred_info = "stolen"
                                                                        print "stolen"
                                                        else:
                                                                beacon_1_lost = beacon_1_lost + 1
                                                else:
                                                        if transferred_info != "safe":
                                                                s_1.sendall('{"ctname":"parking_state", "con":"safe"}')
                                                                transferred_info = "safe"
                                                        print "safe" 
                                                        beacon_1_lost = 0
                                        
                                        # location_data that is data that transferred to parking_location latest.        
                                        if location_data != 39:
                                                s_1.sendall('{"ctname":"parking_location", "con":"39"}')
                                                location_data = 39
                                        
                                elif f1_distance == 100.0:
                                        print "234 beacon"
                                        if f2_distance == 0.0:
                                                print "234 beacon is connected"
                                                if transferred_info != "safe":
                                                        s_1.sendall('{"ctname":"parking_state", "con":"safe"}')
                                                        transferred_info = "safe"
                                                        print "safe" 
                                                f1_distance = 100.0
                                                f2_distance = distance_2
                                        else:
                                                if abs(distance_2 - f2_distance) > 0.5:
                                                        if beacon_2_lost > 1:
                                                                if transferred_info != "stolen":
                                                                        s_1.sendall('{"ctname":"parking_state", "con":"stolen"}')
                                                                        transferred_info = "stolen"
                                                                        print "stolen"
                                                        else:
                                                                beacon_2_lost = beacon_2_lost + 1
                                                else:
                                                        if transferred_info != "safe":
                                                                s_1.sendall('{"ctname":"parking_state", "con":"safe"}')
                                                                transferred_info = "safe"
                                                        beacon_2_lost = 0
                                        if location_data != 234:
                                                s_1.sendall('{"ctname":"parking_location", "con":"234"}')
                                                location_data = 234

                        else: # deactivate the beacon
                                time.sleep(2)
                                f1_distance = 0.0
                                f2_distance = 0.0
                                count_1 = 0

                                

mode_check = ModeCheck()
mode_check.start()

beacon_finder = BeaconFinder()
beacon_finder.start()

beacon_condition = BeaconCondition()
beacon_condition.start()



# (DB) parking_location => location of the bicycle(beacon major number)
# (DB) parking_mode => parking mode(1) or driving mode(0) => check the data and actuate the beacon
# (DB) parking_state => "stolen", "connbeacon", "safe", "nobeacon" 
