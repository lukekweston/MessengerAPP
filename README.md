# MessengerApp
Android App and API for sending messages and having conversations


Add documents about the environment set up, app, server fire base

Add documents about how a message is sent

Add doucuments on how an image is sent

APP)

1) Take photo
2) Orientate the photo correctly
3) Save full image to images/MessengerMvvm
4) Start Async task
5) Save a message row in the database linking to the saved location, will trigger messages live data and make the ui reload
6) Compress the image in the phone - makes it so the api wont be overloaded and run slow
7) Save compressed photo in hidden folder (all compressed photos will be deleted on logout)
8) Update the message created in 5, will get ui to update
9) API call sending the base64 strings for full image and compressed image

API
1) Gets high and low res photos
2) Saves them in folders and links the path in the database (Improves database performance)
3) Sends push notifications/messages to everyone in the chat that there is a photo ready to be downloaded (app will only download compressed photo 
unless the user opens a photo, then another call will be made to get the large photo)
4) responds ok, with saved message information

APP
10) Gets the response, and then updates the local message saved in the database