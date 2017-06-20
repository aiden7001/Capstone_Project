# import the necessary packages
#from scipy.spatial import distance as dist
from imutils import face_utils
import numpy as np
import imutils
import dlib
import cv2
import time
import picamera
import MySQLdb as mydb

## success
#FOR REGISTERING CUSTOMER
## landmark
## can check face_size and extract values of specific section
## gain values(size) and ratio of each section
##take picture and other processing
##db connection and upload our face info in database.

##variable
arr_leye = [0]*6
arr_reye = [0]*6
arr_nose = [0]*9
arr_jaw_x = [0]*17
arr_jaw_y = [0]*17
str_array=[0]*20

##set up
#customer
str_array = raw_input("use id:")
print "Look ahead!"

#camera
cam = picamera.PiCamera()
time.sleep(1)
cam.capture('ttt.jpg')
print "Capture your face successfully"

#image        
landmark = "shape_predictor_68_face_landmarks.dat"
image = cv2.imread("ttt.jpg")
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

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

## detect faceS
rects = detector(gray, 0)

for (i, rect) in enumerate(rects):
	## determine the facial landmarks and convert the values as (x, y)
	shape = predictor(gray, rect)
	shape = face_utils.shape_to_np(shape)

	#draw them on the image
	for (x, y) in shape:
                cv2.circle(image, (x, y), 2, (0, 255, 0), -1)

	##choose landmark(x,y) of specific section(eye, mouth, etc...)
	lefteye = shape[lstart:lend]
	righteye = shape[rstart:rend]
	mouth = shape[mstart:mend]
	nose = shape[nstart:nend]
	jaw = shape[jstart:jend]

	##print landmark values of each section
	i = 0
	for(m,n) in lefteye:
                arr_leye[i] = m
                i = i+1
        i = 0
        for(m,n) in righteye:
                arr_reye[i] = m
                i = i+1

        i = 0
        for(m,n) in nose:
                arr_nose[i] = m
                i = i+1

        i = 0 
        for(m,n) in jaw:
                arr_jaw_x[i] = m
                arr_jaw_y[i] = n
                i = i+1

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
sum_ratio = Face_ratio + LeftEye_Face_ratio + RightEye_Face_ratio + BetweenEye_Face_ratio + Nose_Face_ratio + LeftEye_to_Face_ratio + RightEye_to_Face_ratio


#DB
sql = "insert into registration_table (id,face,lefteye_face,righteye_face,betweeneye_face,nose_face,lefteye_to_face,righteye_to_face, sum_ratio) values(%s,%s,%s,%s,%s,%s,%s,%s,%s)"
cur.execute(sql,(str_array,Face_ratio,LeftEye_Face_ratio,RightEye_Face_ratio,BetweenEye_Face_ratio,Nose_Face_ratio,LeftEye_to_Face_ratio,RightEye_to_Face_ratio,sum_ratio))

sql_main = "insert into compare_table values(%s, %s);"
cur.execute(sql_main,(str_array,sum_ratio))

conn.commit()
print "upload successfully"


## show the output image with the face detections + facial landmarks
cv2.imshow("Output", image)
cv2.waitKey(0)
