### Imports ###################################################################
 
from picamera.array import PiRGBArray
from picamera import PiCamera
 
import time
import cv2
import os
import pygame
import math
import ctypes

from ctypes import *
 
 
### Setup #####################################################################
 
os.putenv( 'SDL_FBDEV', '/dev/fb1' )
print 'success'
#print 'if any people are in the parking lot for some seconds, we can send signal.'
#print 'for particular time there  are "some"  people in parking lot, we can send signal'
# it is clear than 4_17_1.py. SAME
# Setup ##################################################
 
#camera setup
camera = PiCamera()
camera.resolution = ( 320, 240 )
camera.framerate = 30
rawCapture = PiRGBArray( camera, size=( 320, 240 ) )
 
#face recognization setup
fcounter = 0
facefind = 0
 
#park space setup
    #time
start_1 = 0
start_2 = 0
start_3 = 0
end_1 = 0
end_2 = 0
end_3 = 0

# Load a cascade file for detecting faces####################################################################
face_for_park_cascade = cv2.CascadeClassifier('/home/pi/opencv/modules/objdetect/src/haarcascade_frontalface_default.xml')
t_start = time.time()
fps = 0
 
### Main ######################################################################
 
# Capture frames from the camera##########################################
for frame in camera.capture_continuous( rawCapture, format="bgr", use_video_port=True ):
 
    image = frame.array
    #it is standard line /  look easy
    cv2.line(image, (100,0),(100,200),(200,255,0),5)
    cv2.line(image, (200,0),(200,200),(200,255,0),5)
    cv2.line(image, (300,0),(300,200),(200,255,0),5)
    cv2.putText(image,"x=100",(100,200),cv2.FONT_HERSHEY_SIMPLEX,0.5,(0,0,255),2)
    cv2.putText(image,"x=200",(200,200),cv2.FONT_HERSHEY_SIMPLEX,0.5,(0,0,255),2)
    cv2.putText(image,"x=300",(300,200),cv2.FONT_HERSHEY_SIMPLEX,0.5,(0,0,255),2)
 
    #if we don't use this fcounter, the face recognition so sensitive that THE NUMBER of face is very changeable.
    # only 1 time of 20, face is recognized. For the other 19times, the recognization is maintained.
    if fcounter == 20:
 
        fcounter = 0
        facefind = 1
        
        # Look for faces in the image using the loaded cascade file#######################
        gray = cv2.cvtColor( image, cv2.COLOR_BGR2GRAY )
        faces_for_park = face_for_park_cascade.detectMultiScale(gray)

        for(x,y,w,h) in faces_for_park:     #people exist
            if x<100:
                print "----------------------------"
                print "person exists SECTION.1"
                if(start_1 == 0):
                    print " start"
                    start_1 = time.clock()
                else:
                    end_1 = time.clock()         
               
            if x>100 and x<200:
                print "----------------------------"
                print "person exists SECTION.2"   
                if(start_2 == 0):
                    print "start"
                    start_2 = time.clock()
                else:
                    end_2 = time.clock()

            if x>200:
                print "---------------------------"
                print "person exists SECTION.3"
                if(start_3 == 0):
                    print "start"
                    start_3 = time.clock()
                else:
                    end_3 = time.clock()

            check = 1

            #WARNING##########################################
            if(end_1 - start_1 > 3.0):
                print "11111111111111111111111" #Warning here
                start_1 = 0
                end_1 = 0
                time.sleep(0.1) #it has to be here because the speed of process is so fast.

            if(end_2 - start_2 > 3.0):
                print "22222222222222222222222" #Warning here
                start_2 = 0
                end_2 = 0
                time.sleep(0.1) #it has to be here because the speed of process is so fast.

            if(end_3 - start_3 > 3.0):
                print "33333333333333333333333" #Warning here
                start_3 = 0
                end_3 = 0
                time.sleep(0.1) #it has to be here because the speed of process is so fast.
                
                
            #face drawing
            num2 = 0
            while num2 <len(faces_for_park):
                for(x,y,w,h) in faces_for_park:
                    cv2.rectangle(image,(x,y),(x+w,y+h),(200,255,0),2)
                    cv2.putText( image, "Face No." + str(num2), ( x, y ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
                    num2 = num2+1

                

        #for end
            #if person go out in 5 sec, reset
            if(check == 1):
                prex = 0 #value of start (display)
                #pprex = pointer(c_int(prex))
                startx = 100 #value of start line
                #pstartx = pointer(c_int(startx))
                endx = 300 #value of end (display)(not line)
                gap = 100
                countx = 0
                sumx = 0 # sum of value where people exist.
                totalx = 7 # sum of binary notation(1+2+4 because there are only 3 sections)
                resultx = 0 #

                while(startx <= endx):
                    for(x,y,w,h) in faces_for_park: # use this because we have to catch 'every x'.
                        if(x>prex and x<=startx):
                            sumx = sumx + pow(2,countx)
                            break
                            
                    prex = startx
                    #print prex
                    startx = startx + gap
                    #print startx
                    countx = countx + 1
                    time.sleep(0.1)


                resultx = totalx - sumx #result notify which section is empty.
                
                if( resultx == 1):
                    start_1 = end_1 = 0
                    
                if( resultx == 2):
                    start_2 = end_2 = 0
                   
                if( resultx == 3):
                    start_1 = end_1 = 0
                    start_2 = end_2 = 0
        
                if( resultx == 4):
                    start_3 = end_3 = 0
                    
                if( resultx == 5):
                    start_1 = end_1 = 0
                    start_3 = end_3 = 0
                    
                if( resultx == 6):
                    start_2 = end_2 = 0
                    start_3 = end_3 = 0

            check = 0

        if(len(faces_for_park) == 0):
            print "NO people"
            start_1 = start_2 = start_3 = 0
            end_1 = end_2 = end_3 = 0
            time.sleep(0.1) 
 
    fcounter = fcounter + 1
 
    # Calculate and show the FPS####################################################################
    fps = fps + 1
    sfps = fps / ( time.time() - t_start )
    cv2.putText( image, "FPS : " + str( int( sfps ) ), ( 10, 10 ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
 
    cv2.imshow( "Frame", image )
    cv2.waitKey( 1 )
 
    # Clear the stream in preparation for the next frame######################################
    rawCapture.truncate( 0 ) 
