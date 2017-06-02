# import the necessary packages
#from scipy.spatial import distance as dist
from imutils import face_utils
from picamera.array import PiRGBArray
from picamera import PiCamera
from ctypes import *

import numpy as np
import imutils
import dlib
import cv2
import os
import ctypes
import time
import picamera
import MySQLdb as mydb

## success
#FOR FIND CUSTOMER
#real time video and capture when any faces is recognized
#transfer real time data to db. can check realtime data
#now i have to compare these data with already stored data. not insert real time data!!!


##variable
fcounter = 0
image_check = 0
gray_check = 0

arr_leye = [0]*6
arr_reye = [0]*6
arr_nose = [0]*9
arr_jaw_x = [0]*17
arr_jaw_y = [0]*17
str_array=[0]*20

##set up
os.putenv('SDL_FBDEV','/dev/fb1')

#camera
camera = PiCamera()
camera.resolution = (320,240)
camera.framerate = 30
rawCapture = PiRGBArray(camera,size=(320,240))


face_cascade = cv2.CascadeClassifier('/home/pi/opencv-3.2.0/modules/objdetect/src/haarcascade_frontalface_default.xml')
t_start = time.time()
fps=0

#image        
landmark = "shape_predictor_68_face_landmarks.dat"
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(landmark)

#section
(lstart, lend) = face_utils.FACIAL_LANDMARKS_IDXS['left_eye']
(rstart, rend) = face_utils.FACIAL_LANDMARKS_IDXS['right_eye']
(mstart, mend) = face_utils.FACIAL_LANDMARKS_IDXS['mouth']
(nstart, nend) = face_utils.FACIAL_LANDMARKS_IDXS['nose']
(jstart, jend) = face_utils.FACIAL_LANDMARKS_IDXS['jaw']

#db
conn = mydb.connect("localhost","root","hyw5782","project_landmark")
cur = conn.cursor()

#
for frame in camera.capture_continuous( rawCapture,format="bgr",use_video_port=True):

        image = frame.array

        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray)

        if fcounter == 20:
                fcounter = 0

                #print "safdfdfasdfasdfsa"#for check

                if(len(faces) != 0):
                        print len(faces)
                        camera.capture('temp.jpg')
                        #print 'Capture your face'#for check

                        image_check = cv2.imread("temp.jpg")
                        gray_check = cv2.cvtColor(image_check, cv2.COLOR_BGR2GRAY)
                        
                        rects = detector(gray_check,0)

                        for(i, rect) in enumerate(rects):
                                shape = predictor(gray_check,rect)
                                shape = face_utils.shape_to_np(shape)

                                for(x,y) in shape:
                                        cv2.circle(image_check,(x,y),2,(0,255,0),-1)

                                lefteye = shape[lstart:lend]
                                righteye = shape[rstart:rend]
                                mouth = shape[mstart:mend]
                                nose = shape[nstart:nend]
                                jaw = shape[jstart:jend]

                                i=0
                                for(m,n) in lefteye:
                                        arr_leye[i] = m
                                        i=i+1
                                i=0
                                for(m,n) in righteye:
                                        arr_reye[i] = m
                                        i=i+1
                                i=0
                                for(m,n) in nose:
                                        arr_nose[i] = m
                                        i=i+1
                                i=0
                                for(m,n) in jaw:
                                        arr_jaw_x[i] = m
                                        arr_jaw_y[i] = n
                                        i=i+1

                #size
                Eye_Left_length = str(max(arr_leye)-min(arr_leye))
                Eye_Right_length = str(max(arr_reye)-min(arr_reye))
                Nose_width = str(max(arr_nose)-min(arr_nose))
                Eye_between = str(min(arr_leye)-max(arr_reye))
                Eye_to_Face_Left = str(max(arr_jaw_x)-max(arr_leye))
                Eye_to_Face_Right = str(min(arr_reye)-min(arr_jaw_x))
                Face_width = str(max(arr_jaw_x)-min(arr_jaw_x))
                Face_length = str(max(arr_jaw_y)-min(arr_jaw_y))

                #ratio
                Face_ratio = round(float(Face_length)/float(Face_width),2)
                LeftEye_Face_ratio = round(float(Eye_Left_length)/float(Face_width),2)
                RightEye_Face_ratio = round(float(Eye_Right_length)/float(Face_width),2)
                BetweenEye_Face_ratio = round(float(Eye_between)/float(Face_width),2)
                Nose_Face_ratio = round(float(Nose_width)/float(Face_width),2)
                LeftEye_to_Face_ratio = round(float(Eye_to_Face_Left)/float(Face_width),2)
                RightEye_to_Face_ratio = round(float(Eye_to_Face_Right)/float(Face_width),2)

                sql = "insert into test_table values(%s,%s,%s,%s,%s,%s,%s);"
                cur.execute(sql,(Face_ratio,LeftEye_Face_ratio,RightEye_Face_ratio,BetweenEye_Face_ratio,Nose_Face_ratio,LeftEye_to_Face_ratio,RightEye_to_Face_ratio))
                conn.commit()


        else:
                if len(faces) != 0:
                        for(x,y,w,h) in faces:
                                cv2.rectangle(image,(x,y),(x+w,y+h),(200,255,0),2)

        

        fcounter = fcounter +1
        print fcounter
        
        fps = fps +1
        sfps = fps / (time.time() - t_start)

        cv2.imshow("Frmae",image)
        cv2.imshow("Check",image_check)
        cv2.waitKey(1)

        rawCapture.truncate(0)
        
