SELECT "Id", "UserName", "UserEmail", "Password"
FROM "MessengerData"."Users"
LIMIT 1000;

SELECT "UserId", "ConversationId", "Id"
FROM "MessengerData"."UserConversation"
LIMIT 1000;

SELECT "Id", "UserId", "TextMessage", "TimeSent", "ConversationId"
FROM "MessengerData"."Messages"
LIMIT 1000;

SELECT "Id", "ConversationName"
FROM "MessengerData"."Conversations"
LIMIT 1000;

INSERT INTO "MessengerData"."Users" ("UserName", "UserEmail", "Password") 
VALUES ('Luke', 'luke@weston.net.nz', 'password');

INSERT INTO "MessengerData"."Users" ("UserName", "UserEmail", "Password") 
VALUES ('Luke2', 'luke+1@weston.net.nz', 'password');

INSERT INTO "MessengerData"."Users" ("UserName", "UserEmail", "Password") 
VALUES ('Jeff', 'luke+2@weston.net.nz', 'password');

INSERT INTO "MessengerData"."Conversations" ("ConversationName") 
VALUES ('Lukes Chat');

INSERT INTO "MessengerData"."Conversations" ("ConversationName") 
VALUES ('');

INSERT INTO "MessengerData"."UserConversation" ("ConversationId", "UserId", "Id") 
VALUES (1, 1, 1);

INSERT INTO "MessengerData"."UserConversation" ("ConversationId", "UserId", "Id") 
VALUES (1, 2, 2);

INSERT INTO "MessengerData"."UserConversation" ("ConversationId", "UserId", "Id") 
VALUES (2, 1, 3);

INSERT INTO "MessengerData"."UserConversation" ("ConversationId", "UserId", "Id") 
VALUES (2, 2, 4);

INSERT INTO "MessengerData"."UserConversation" ("ConversationId", "UserId", "Id") 
VALUES (2, 3, 5);

INSERT INTO "MessengerData"."Messages" ("UserId", "TextMessage", "TimeSent", "ConversationId") 
VALUES (1, 'Hey', NOW(), 1);

INSERT INTO "MessengerData"."Messages" ("UserId", "TextMessage", "TimeSent", "ConversationId") 
VALUES (2, 'Hey?????', NOW(), 1);

INSERT INTO "MessengerData"."Messages" ("UserId", "TextMessage", "TimeSent", "ConversationId") 
VALUES (1, 'What are you up to?', NOW(), 1);

INSERT INTO "MessengerData"."Messages" ("UserId", "TextMessage", "TimeSent", "ConversationId") 
VALUES (2, 'nm, whats up with you?', NOW(), 1);

INSERT INTO "MessengerData"."Messages" ("UserId", "TextMessage", "TimeSent", "ConversationId") 
VALUES (1, 'Just chilling and enterind a lil bit of mock data', NOW(), 1);

DO $$ 
DECLARE 
  conversationId INTEGER := 1;
  userId1 INTEGER := 1;
  userId2 INTEGER := 2;
  greetings1 VARCHAR(50) := 'Hey';
  greetings2 VARCHAR(50) := 'Hi there!';
  questions1 VARCHAR(50) := 'How are you doing?';
  questions2 VARCHAR(50) := 'I am doing well, thank you for asking.';
  comments1 VARCHAR(50) := 'That''s great!';
  comments2 VARCHAR(50) := 'Yeah, I''m happy about it.';
  timeSent TIMESTAMP := NOW();
BEGIN
  -- Insert first two messages
  INSERT INTO "MessengerData"."Messages" ("UserId", "TextMessage", "TimeSent", "ConversationId