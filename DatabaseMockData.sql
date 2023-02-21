/****** Script for SelectTopNRows command from SSMS  ******/
SELECT TOP (1000) [Id]
      ,[UserName]
      ,[UserEmail]
      ,[Password]
  FROM [MessengerData].[dbo].[Users]


  SELECT TOP (1000) [UserId]
      ,[ConversationId]
      ,[Id]
  FROM [MessengerData].[dbo].[UserConversation]

  SELECT TOP (1000) [Id]
      ,[UserId]
      ,[TextMessage]
      ,[TimeSent]
      ,[ConversationId]
  FROM [MessengerData].[dbo].[Messages]


  SELECT TOP (1000) [Id]
      ,[ConversationName]
  FROM [MessengerData].[dbo].[Conversations]

  insert into Users (UserName, UserEmail, Password) values ('Luke', 'luke@weston.net.nz', 'password')
  insert into Users (UserName, UserEmail, Password) values ('Luke2', 'luke+1@weston.net.nz', 'password')
  insert into Users (UserName, UserEmail, Password) values ('Jeff', 'luke+2@weston.net.nz', 'password')

  insert into Conversations (ConversationName) values ('Lukes Chat')
  insert into Conversations (ConversationName) values ('')

  insert into UserConversation (ConversationId, UserId, id) values(1, 1, 1)
  insert into UserConversation (ConversationId, UserId, id) values(1, 2, 2)

  insert into UserConversation (ConversationId, UserId, id) values(2, 1, 3)
  insert into UserConversation (ConversationId, UserId, id) values(2, 2, 4)
  insert into UserConversation (ConversationId, UserId, id) values(2, 3, 5)


  insert into [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId]) values (1, 'Hey', GETDATE(), 1) 
   insert into [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId]) values (2, 'Hey?????', GETDATE(), 1) 
   insert into [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId]) values (1, 'What are you up to?', GETDATE(), 1) 
   insert into [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId]) values (2, 'nm, whats up with you?', GETDATE(), 1) 
   insert into [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId]) values (1, 'Just chilling and enterind a lil bit of mock data', GETDATE(), 1) 

   DECLARE @conversationId INT = 1
DECLARE @userId1 INT = 1
DECLARE @userId2 INT = 2
DECLARE @greetings1 VARCHAR(50) = 'Hey'
DECLARE @greetings2 VARCHAR(50) = 'Hi there!'
DECLARE @questions1 VARCHAR(50) = 'How are you doing?'
DECLARE @questions2 VARCHAR(50) = 'I am doing well, thank you for asking.'
DECLARE @comments1 VARCHAR(50) = 'That''s great!'
DECLARE @comments2 VARCHAR(50) = 'Yeah, I''m happy about it.'
DECLARE @timeSent DATETIME = GETDATE()

-- Insert first two messages
INSERT INTO [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId])
VALUES (@userId1, @greetings1, @timeSent, @conversationId)

SET @timeSent = DATEADD(SECOND, 3, @timeSent)

INSERT INTO [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId])
VALUES (@userId2, @greetings2, @timeSent, @conversationId)

-- Insert 8 additional messages
DECLARE @i INT = 3
WHILE @i <= 10
BEGIN
    IF (@i % 2) = 1
    BEGIN
        SET @timeSent = DATEADD(SECOND, RAND() * 20, @timeSent) -- random delay between 0 and 20 seconds
        IF (@i % 4) = 3 -- every third message from user 1 is a question
        BEGIN
            INSERT INTO [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId])
            VALUES (@userId1, @questions1, @timeSent, @conversationId)
            SET @timeSent = DATEADD(SECOND, 2, @timeSent)
            INSERT INTO [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId])
            VALUES (@userId2, @questions2, @timeSent, @conversationId)
        END
        ELSE
        BEGIN
            INSERT INTO [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId])
            VALUES (@userId1, @comments1, @timeSent, @conversationId)
            SET @timeSent = DATEADD(SECOND, 2, @timeSent)
            INSERT INTO [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId])
            VALUES (@userId2, @comments2, @timeSent, @conversationId)
        END
    END
    ELSE
    BEGIN
        SET @timeSent = DATEADD(SECOND, RAND() * 15, @timeSent) -- random delay between 0 and 15 seconds
        INSERT INTO [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId])
        VALUES (@userId2, 'Yeah, I''m here. What''s up?', @timeSent, @conversationId)
        SET @timeSent = DATEADD(SECOND, 2, @timeSent)
        INSERT INTO [Messages] ([UserId], [TextMessage], [TimeSent], [ConversationId])
        VALUES (@userId1, 'Not much, just working. You?', @timeSent, @conversationId)
    END
    SET @i = @i + 1
END

 