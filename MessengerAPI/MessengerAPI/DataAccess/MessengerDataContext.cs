using MessengerAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace MessengerAPI.DataAccess
{
    public class MessengerDataContext : DbContext
    {

        public MessengerDataContext(DbContextOptions<MessengerDataContext> options) : base(options)
        {

        }

        public DbSet<Conversation> Conversations { get; set; }
        public DbSet<Message> Messages { get; set; }
        public DbSet<User> Users { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<UserConversation>()
                .HasKey(uc => new { uc.UserId, uc.ConversationId });
            modelBuilder.Entity<UserConversation>()
                .HasOne(uc => uc.User)
                .WithMany(u => u.UserConversations)
                .HasForeignKey(uc => uc.UserId);
            modelBuilder.Entity<UserConversation>()
                .HasOne(uc => uc.Conversation)
                .WithMany(c => c.UserConversations)
                .HasForeignKey(uc => uc.ConversationId);


            modelBuilder.Entity<Conversation>()
            .HasMany(c => c.Messages)
            .WithOne(m => m.Conversation)
            .HasForeignKey(m => m.ConversationId);



        }


        public Conversation? GetConversation(int conversationId) => Conversations.Where(c => c.Id == conversationId).FirstOrDefault();
        public List<Message> GetMessagesForAConversation(int conversationId) => Messages.Where(m => m.ConversationId == conversationId).ToList();
        public List<Conversation> GetConversationsForAUser(int userId) => Conversations.Where(u => u.Id== userId).ToList();
        public User? GetUserIdForUserName(string userName) => Users.Where(u => u.UserName == userName).FirstOrDefault();

    }
}
