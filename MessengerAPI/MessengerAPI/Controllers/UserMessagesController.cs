using MessengerAPI.DataAccess;
using MessengerAPI.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

namespace MessengerAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UserMessagesController : Controller
    {

        private readonly MessengerDataContext _context;

        public UserMessagesController(MessengerDataContext context)
        {
            _context = context;
        }

        public class ConversationInfo
        {
            public int ConversationId { get; set; }
            public string ConversationName { get; set; }
            public List<UserInfo> OtherParticipants { get; set; }
        }

        public class UserInfo
        {
            public int UserId { get; set; }
            public string UserName { get; set; }
        }



        [HttpGet("GetConversationsForUser/{userId}")]
        public List<ConversationInfo> GetConversationData(int userId)
        {
            var userConversations = _context.GetUserConversationsForUser(userId);

            var result = new List<ConversationInfo>();

            foreach (var userConversation in userConversations)
            {
                //var otherUsers = _context.UserConversation.Where(uc =>
                //                (userConversations.Select(u => u.ConversationId).ToList().Contains(uc.ConversationId) && uc.UserId != userId)).Select(uc => uc.UserId).ToList();

                var otherUsers = _context.UserConversation.Where(uc => uc.ConversationId == userConversation.Id && uc.UserId != userId).Select(uc => uc.UserId).ToList();

                var otherUsersInfo = _context.Users.Where(u => otherUsers.Contains(u.Id)).Select(u =>
                    new UserInfo
                    {
                        UserId = u.Id,
                        UserName = u.UserName
                    }
                ).ToList();

                //Get the conversation name
                var conversationName = _context.Conversations.Where(c => c.Id == userConversation.ConversationId).Select(u => u.ConversationName).FirstOrDefault();

                //if the conversation name isnt set
                if (conversationName.IsNullOrEmpty())
                {
                    //If there are other users create the conversation name by concating the other userNames together
                    if (otherUsers.Count() > 0)
                    {
                        conversationName += otherUsersInfo[0].UserName;
                        for (int i = 1; i < otherUsersInfo.Count(); i++)
                        {
                            conversationName += ", " + otherUsersInfo[i].UserName;
                        }
                        foreach (UserInfo ui in otherUsersInfo)
                        {

                        }
                    }
                    //Else the conversation name will be to yourself so it will be named your name
                    else
                    {
                        conversationName = _context.Users.Where(u => u.Id == userId).Select(u => u.UserName).First();
                    }

                }

                result.Add(new ConversationInfo
                {
                    ConversationId = userConversation.ConversationId,
                    ConversationName = conversationName,
                    OtherParticipants = otherUsersInfo
                });
            }

            return result;

        }











        //// GET: UserMessagesController
        //public ActionResult Index()
        //{
        //    return View();
        //}

        //// GET: UserMessagesController/Details/5
        //public ActionResult Details(int id)
        //{
        //    return View();
        //}

        //// GET: UserMessagesController/Create
        //public ActionResult Create()
        //{
        //    return View();
        //}

        //// POST: UserMessagesController/Create
        //[HttpPost]
        //[ValidateAntiForgeryToken]
        //public ActionResult Create(IFormCollection collection)
        //{
        //    try
        //    {
        //        return RedirectToAction(nameof(Index));
        //    }
        //    catch
        //    {
        //        return View();
        //    }
        //}

        //// GET: UserMessagesController/Edit/5
        //public ActionResult Edit(int id)
        //{
        //    return View();
        //}

        //// POST: UserMessagesController/Edit/5
        //[HttpPost]
        //[ValidateAntiForgeryToken]
        //public ActionResult Edit(int id, IFormCollection collection)
        //{
        //    try
        //    {
        //        return RedirectToAction(nameof(Index));
        //    }
        //    catch
        //    {
        //        return View();
        //    }
        //}

        //// GET: UserMessagesController/Delete/5
        //public ActionResult Delete(int id)
        //{
        //    return View();
        //}

        //// POST: UserMessagesController/Delete/5
        //[HttpPost]
        //[ValidateAntiForgeryToken]
        //public ActionResult Delete(int id, IFormCollection collection)
        //{
        //    try
        //    {
        //        return RedirectToAction(nameof(Index));
        //    }
        //    catch
        //    {
        //        return View();
        //    }
        //}
    }
}
