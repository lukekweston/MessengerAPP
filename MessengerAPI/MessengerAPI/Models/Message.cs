namespace MessengerAPI.Models
{
    public class Message
    {
        public int Id { get; set; }       
        public int UserId { get; set; }
#nullable enable
        public string? TextMessage { get; set; }
        public DateTime TimeSent { get; set; }
        public DateTime? UpdatedTime { get; set; }
#nullable disable

        public int ConversationId { get; set; }
        public Conversation Conversation { get; set; }
    }
}
