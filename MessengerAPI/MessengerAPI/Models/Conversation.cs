namespace MessengerAPI.Models
{
    public class Conversation
    {
        public int Id { get; set; }
#nullable enable
        public string? ConversationName { get; set; }
        public DateTime? LastUpdated { get; set; }
#nullable disable

        public virtual ICollection<UserConversation> UserConversations { get; set; }
        public virtual ICollection<Message> Messages { get; set; }
    }
    
}
