
### Imports ###################################################################

from picamera.array import PiRGBArray
from picamera import PiCamera

import time
import cv2
import os
import pygame


### Setup #####################################################################

os.putenv( 'SDL_FBDEV', '/dev/fb1' )
print 'face succeess and diferent numbering success'
# we can see that the video has some tic slightly because we use 'while(num2<len(faces))'
# so, if we change the WHILE to FOR, we can not recongnize or differentiate the face exactly duto fast speed


# Setup the camera##################################################
camera = PiCamera()
camera.resolution = ( 320, 240 )
camera.framerate = 30
rawCapture = PiRGBArray( camera, size=( 320, 240 ) )

fcounter = 0
facefind = 0
face_array= [0,0,0,0,0,0,0]

# Load a cascade file for detecting faces####################################################################
face_cascade = cv2.CascadeClassifier( '/home/pi/opencv/modules/objdetect/src/haarcascade_frontalface_default.xml' )

t_start = time.time()
fps = 0

### Main ######################################################################

# Capture frames from the camera##########################################
for frame in camera.capture_continuous( rawCapture, format="bgr", use_video_port=True ):

    image = frame.array

    if fcounter == 20:

        fcounter = 0
        
        # Look for faces in the image using the loaded cascade file#######################
        gray = cv2.cvtColor( image, cv2.COLOR_BGR2GRAY )
        faces = face_cascade.detectMultiScale( gray ) #take faces simultaneosly

        print "--------------------------------------------------------------------------------------------"
        print "Found " + str( len( faces ) ) + " face(s)"

        if str(len(faces)) !=0:
            facefind = 1
            num2 = 0

            # signal
            #here
        while num2 < len(faces):
            for(x,y,w,h) in faces:
                print "******************"
                cv2.rectangle( image, ( x, y ), ( x + w, y + h ), ( 200, 255, 0 ), 2 )
                cv2.putText( image, "Face No." + str(num2), ( x, y ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
                num2 = num2+1

    else:
        if facefind == 1 and str(len(faces)) != 0:
            while num2 < len(faces):
                for(x,y,w,h) in faces:
                    print "******************"
                    cv2.rectangle( image, ( x, y ), ( x + w, y + h ), ( 200, 255, 0 ), 2 )
                    cv2.putText( image, "Face No." + str(num2), ( x, y ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )
                    
                    num2 = num2+1

    fcounter = fcounter + 1
 
    # Calculate and show the FPS####################################################################
    fps = fps + 1
    sfps = fps / ( time.time() - t_start )
    cv2.putText( image, "FPS : " + str( int( sfps ) ), ( 10, 10 ), cv2.FONT_HERSHEY_SIMPLEX, 0.5, ( 0, 0, 255 ), 2 )

    cv2.imshow( "Frame", image )
    cv2.waitKey( 1 )

    # Clear the stream in preparation for the next frame######################################
    rawCapture.truncate( 0 ) 
