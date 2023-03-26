# MessengerApp
Android App and API for sending messages and having conversations

[![IMAGE ALT TEXT](http://img.youtube.com/vi/E7sOMdqAuEs/0.jpg)](http://www.youtube.com/watch?v=E7sOMdqAuEs "Messenger app demonstration")


```mermaid
graph TD;
    subgraph "Server"
        "PostgresSQL Database"--> "SpringBoot API";
        "SpringBoot API"-->Firebase;
    end
    subgraph "Phone 1"
        "SpringBoot API"--> "Phone 1";
        "Phone 1"--> "Room Database 1";
        "Phone 1"<-->Firebase;
    end
    subgraph "Phone 2"
        "SpringBoot API"--> "Phone 2";
        "Phone 2"--> "Room Database 2";
        "Phone 2"<-->Firebase;
    end
```



Add documents about the environment setup, app, server firebase

Add documents about how a message is sent

Add documents on how an image is sent

APP)

1) Take a photo
2) Orientate the photo correctly
3) Save the full image to images/MessengerMvvm
4) Start the Async task
5) Save a message row in the database linking to the saved location, which will trigger messages' live data and make the UI reload
6) Compress the image in the phone - makes it so the API won't be overloaded and run slow
7) Save compressed photos in a hidden folder (all compressed photos will be deleted on logout)
8) Update the message created in 5, will get UI to update
9) API call sending the base64 strings for full image and compressed image

API
1) Gets high and low res photos
2) Saves them in folders and links the path to the database (Improves database performance)
3) Sends push notifications/messages to everyone in the chat that there is a photo ready to be downloaded (the app will only download compressed photo 
unless the user opens a photo, then another call will be made to get the large photo)
4) responds ok, with saved message information

APP
10) Gets the response, and then updates the local message saved in the database



Todo - documentation

1) Document - overall how the app works
2) Libraries used
3) How a message is sent
4) How photos are sent
5) How friends work

Todo features

1) Oauth2
2) <s>Dagger hilt dependency injection</s> 15/3/23
3) allow the users to start group conversations, these group conversation the user should be able to add their friends to it
4) Delete messages
5) Seen status on messages
6) Add a profile screen - show the profile image on the messages screen
7) <s>Dark mode</s> 14/3/22 - theme doesn't look great though
8) Make the home page go to a maintenance screen if the server is not accessible 

Todo code fixes

1) <s>Move checking if the user is logged in to the splash screen - avoid login screen if they are logged in</s> completed 13/03/23
2) <s>Cache the full res image - don't cache other api queries (no others need to be cached)</s> 14/3/22 
3) Fix the conversation title in the new message notification
4) Increase offline support - not sent messages to display differently, messages are sent once the phone has a connection again
5) Change the Messages activity to open the camera/gallery using the activity result contract - See if I can add more loading icons to the flow
6) See if it is possible to return the image as a base 64 string from the camera, not a byte array
7) Change the low res images to have their uri saved in the database, not their path
8) Changes MessagesViewModel.sendImage to use a coroutine that lasts longer than the lifetime of the view model - a large image may take a while to compress and send
(also check if this is a problem, is the view model destroyed on activity close)
9) API - delete all the images where the message is no longer in the database
10) Allow for blocking a person
11) Look into why messages are not appearing in order straight after a message is sent
12) Tidy up the push notification file
