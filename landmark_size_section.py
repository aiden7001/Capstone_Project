# import the necessary packages
#from scipy.spatial import distance as dist
from imutils import face_utils
import numpy as np
import imutils
import dlib
import cv2

## success
## landmark
## can check face_size and extract values of specific section
## gain values(size) of each section

##variable
#x_matrix = [0]*600
#y_matrix = [0]*600
#face_size = 0
arr_leye = [0]*6
arr_reye = [0]*6
arr_nose = [0]*9
arr_jaw_x = [0]*17
arr_jaw_y = [0]*17

#set up
landmark = "shape_predictor_68_face_landmarks.dat"
#lefteye_cascade = cv2.CascadeClassifier('/home/pi/opencv-3.2.0/modules/objdetect/src/lefteye.xml')
image = cv2.imread("face_1.jpg")
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(landmark)
#lefteye = lefteye_cascade.detectMultiScale(gray)
(lstart, lend) = face_utils.FACIAL_LANDMARKS_IDXS['left_eye']
(rstart, rend) = face_utils.FACIAL_LANDMARKS_IDXS['right_eye']
(mstart, mend) = face_utils.FACIAL_LANDMARKS_IDXS['mouth']
(nstart, nend) = face_utils.FACIAL_LANDMARKS_IDXS['nose']
(jstart, jend) = face_utils.FACIAL_LANDMARKS_IDXS['jaw']

## detect faces in the grayscale image
rects = detector(gray, 0)

#for(leye_x,leye_y,leye_w,leye_h) in lefteye:
#        cv2.rectangle(image,(leye_x,leye_y),(leye_x+leye_w,leye_y+leye_h),(200,255,0),2)

        

## loop over the face detections
for (i, rect) in enumerate(rects):
	## determine the facial landmarks for the face region, then
	## convert the facial landmark (x, y)
	shape = predictor(gray, rect)
	shape = face_utils.shape_to_np(shape)

	# loop over the (x, y)
	# and draw them on the image
	for (x, y) in shape:
                cv2.circle(image, (x, y), 2, (0, 255, 0), -1)

	##choose landmark(x,y) of specific section(eye, mouth, etc...)
	lefteye = shape[lstart:lend]
	righteye = shape[rstart:rend]
	mouth = shape[mstart:mend]
	nose = shape[nstart:nend]
	jaw = shape[jstart:jend]

	##print landmark values of each section
	print 'Left Eye----------------------------' #for check
	i = 0
	for(m,n) in lefteye:
                print ( '(' + str(m) + ' ' + str(n) + ')' )
                arr_leye[i] = m
                i = i+1
                
        print 'Right Eye----------------------------' #for check
        i = 0
        for(m,n) in righteye:
                print( '(' + str(m) + ' ' + str(n) + ')' )
                arr_reye[i] = m
                i = i+1
                
        print 'Mouth-------------------------------' #for check
        for(m,n) in mouth:
                print( '(' + str(m) + ' ' + str(n) + ')' )

                
        print 'nose-------------------------------' #for check
        i = 0
        for(m,n) in nose:
                print( '(' + str(m) + ' ' + str(n) + ')' )
                arr_nose[i] = m
                i = i+1

        print 'jaw-------------------------------' #for check
        i = 0 
        for(m,n) in jaw:
                print( '(' + str(m) + ' ' + str(n) + ')' )
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


print 'Left Eye Length = ' +  Eye_Left_length #for check
print 'Right Eye Length = ' + Eye_Right_length #for check
print 'Nose Width = ' + Nose_width #for check
print 'Between Eye Length = ' + Eye_between #for check
print 'LeftSide Eye to face = ' + Eye_to_Face_Left #for check
print 'RightSide Eye to face = ' + Eye_to_Face_Right #for check
print 'Face_width = ' + Face_width #for check
print 'Face_length = ' + Face_length #for check






## show the output image with the face detections + facial landmarks
cv2.imshow("Output", image)
cv2.waitKey(0)
