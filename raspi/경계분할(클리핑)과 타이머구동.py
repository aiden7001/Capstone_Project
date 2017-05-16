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
getin = 0
getout = 0
getin_count = 0
getout_count = 0

count_park = 0
start_time = 0
end_time = 0
result_time = 0

#congestion
mutex = 0

# Load a cascade file for detecting faces####################################################################
face_cascade = cv2.CascadeClassifier( '/home/pi/opencv/modules/objdetect/src/haarcascade_frontalface_default.xml' )
face_for_park_cascade = cv2.CascadeClassifier('/home/pi/opencv/modules/objdetect/src/haarcascade_frontalface_default.xml')
t_start = time.time()
fps = 0
 
### Main ######################################################################
 
# Capture frames from the camera##########################################
for frame in camera.capture_continuous( rawCapture, format="bgr", use_video_port=True ):
 
    image = frame.array
    #(x,y) = <from upperside> like no.4 section
    #it is for park space.
    cv2.line(image, (250, 0), (250,200), (0,0,255), 5)
    cv2.line(image, (50,0),(50,200),(0,0,255),5)
    cv2.putText( image, "x=250", ( 250, 200 ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
    cv2.putText( image, "x=50" , ( 50, 200), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
    #it is standard line /  look easy
    cv2.line(image, (150,0),(150,200),(200,255,0),5)
    cv2.putText(image,"x=150",(150,200),cv2.FONT_HERSHEY_SIMPLEX,0.5,(0,0,255),2)
    
 
    #if we don't use this fcounter, the face recognition so sensitive that THE NUMBER of face is very changeable.
    # only 1 time of 20, face is recognized. For the other 19times, the recognization is maintained.
    if fcounter == 20:
 
        fcounter = 0
        facefind = 1
        
        # Look for faces in the image using the loaded cascade file#######################
        gray = cv2.cvtColor( image, cv2.COLOR_BGR2GRAY )
        faces_for_park = face_for_park_cascade.detectMultiScale(gray)
 
        for(x,y,w,h) in faces_for_park:
            #print"----------------------------------------------------------"
            #print "Found " + str(len(faces_for_park)) + " face(s)"

            if(mutex == 0):
                
                if x>150 and x<=250: 
                    print " get IN READY"
                    getin = 1

                elif(x<150 and x>50):
                    print "get OUT READY"
                    getout = 1

 
                elif(getin == 1):
                    if(x<50):
                        print " get IN"
                        getin_count  = getin_count +1
                        print "start_time = " + str(start_time)
                        start_time = time.clock()

                elif(getout==1):
                    if(x>250):
                        print "get OUT"
                        getout_count = getout_count +1

                mutex = 1
                time.sleep(0.1)

            elif(mutex == 1):
                #if(getin_count != getout_count):
                print "sdfasdfsfsfasfasdfsafasdfsdfasf"
                end_time = time.clock()
                result_time = end_time - start_time
                print"result_time = " + str(result_time)

                if(result_time > 7):
                    print "xxxxxxxxx  WARNING xxxxxxxxxxxx"

                elif(getin_count == getout_count):
                    start_time = 0
                    end_time = 0

                mutex = 0

            #face drawing
            num2 = 0
            while num2 <len(faces_for_park):
                for(x,y,w,h) in faces_for_park:
                    cv2.rectangle(image,(x,y),(x+w,y+h),(200,255,0),2)
                    cv2.putText( image, "Face No." + str(num2), ( x, y ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
                    num2 = num2+1

                        
            #if(result_time >= 5.0): 
            #    print"xxxxxxxxxxxxxxxxxxx    WARNING   xxxxxxxxxxxxxxxxxxxxxxxx"
            #    start_time = 0
            #    end_time = 0
                
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
