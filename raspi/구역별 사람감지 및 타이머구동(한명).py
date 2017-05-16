### Imports ###################################################################
 
from picamera.array import PiRGBArray
from picamera import PiCamera
 
import time
import cv2
import os
import pygame
 
 
### Setup #####################################################################
 
os.putenv( 'SDL_FBDEV', '/dev/fb1' )
print 'succss'
print 'if any people are in the parking lot for some seconds, we can send signal.'
print 'for particular time there is "one"  person in parking lot, we can send signal'
# we can see that the video has some tic slightly because we use 'while(num2<len(faces))'
# so, if we change the WHILE to FOR, we can not recongnize or differentiate the face exactly duto fast speed
 
 
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
    #count
count_1 = 0
count_1_2 = 0
count_2 = 0
count_3 = 0
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
                print "----------------------------------------------"
                print "person exists SECTION.1"
                if(start_1 == 0):
                    print " start"
                    start_1 = time.clock()
                else:
                    print "end"
                    end_1 = time.clock()
                    print end_1 - start_1              
               
            if x>100 and x<200:
                print "-----------------------------------------------"
                print "person exist SECTION.2"   
                if(start_2 == 0):
                    print "start"
                    start_2 = time.clock()
                else:
                    print "end"
                    end_2 = time.clock()
                    print end_2-start_2

            if x>200:
                print "-------------------------------------------"
                print "person exist SECTION.3"
                if(start_3 == 0):
                    print "start"
                    start_3 = time.clock()
                else:
                    print "end"
                    end_3 = time.clock()
                    print end_3-start_3

            #WARNING##########################################
            if(end_1 - start_1 > 5.0):
                print "11111111111111111111111" #Warning here
                start_1 = 0
                end_1 = 0
                time.sleep(0.1) #it has to be here because the speed of process is so fast.

            if(end_2 - start_2 > 5.0):
                print "22222222222222222222222" #Warning here
                start_2 = 0
                end_2 = 0
                time.sleep(0.1) #it has to be here because the speed of process is so fast.

            if(end_3 - start_3 > 5.0):
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
        if(len(faces_for_park) != 0):
            print "-------------------------------------------"
            if not(x<100):
                start_1 = end_1 = 0
                print "start_1 = " + str(start_1)
                
                time.sleep(0.1)
            if not(x>100 and x<200):
                start_2 = end_2 = 0
                print "start_2 = " + str(start_2)
                time.sleep(0.1)
            if not(x>200):
                start_3 = end_3 = 0
                print "start_3 = " + str(start_3)
                time.sleep(0.1)

            #if not(x<200):      #only section 3
            #    start_1 = start_2 = 0
            #    print "start_1 = " + str(start_1)
            #    print "start_2 = " + str(start_2)
            #    time.sleep(0.1)

            #if not(x>100):      #only section 1
            #    start_2 = start_3 = 0
            #    print "start_2 = " + str(start_2)
            #    print "start_3 = " + str(start_3)
            #    time.sleep(0.1)

            #if not(x>100 and x<200):    #only section 2
            #    start_1 = start_3 = 0
            #    print "start_1 = " + str(start_1)
            #    print "start_3 = " + str(start_3)
                

        if(len(faces_for_park) == 0):
            print "NO people"
            start_1 = start_2 = start_3 = 0
            end_1 = end_2 = end_3 = 0
            time.sleep(0.1)
            
                
    #else:
    #    if facefind == 1 and str(len(faces_for_park)) != 0:
    #        for(x,y,w,h) in faces_for_park:
    #            if(x,y,w,h)<(60,60,40,40):
    #                while num2 <len(faces_for_park):
    #                    cv2.rectangle( image, ( x, y ), ( x + w, y + h ), ( 200, 255, 0 ), 2 )
    #                    cv2.putText( image, "Face No." + str(num2), ( x, y ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
    #                    num2 = num2+1
        
 
 
    fcounter = fcounter + 1
 
    # Calculate and show the FPS####################################################################
    fps = fps + 1
    sfps = fps / ( time.time() - t_start )
    cv2.putText( image, "FPS : " + str( int( sfps ) ), ( 10, 10 ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
 
    cv2.imshow( "Frame", image )
    cv2.waitKey( 1 )
 
    # Clear the stream in preparation for the next frame######################################
    rawCapture.truncate( 0 ) 
