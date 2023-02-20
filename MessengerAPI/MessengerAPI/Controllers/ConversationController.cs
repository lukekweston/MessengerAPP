using MessengerAPI.DataAccess;
using MessengerAPI.Models;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using RouteAttribute = Microsoft.AspNetCore.Components.RouteAttribute;

namespace MessengerAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ConversationController : ControllerBase
    {

        private readonly MessengerDataContext _context;

        public ConversationController(MessengerDataContext context)
        {
            _context = context;
        }


        [HttpGet("GetConversaion/{convId}")]
        public async Task<ActionResult<Conversation?>> getConversationData(int convId)
        {
            return await _context.Conversations.Where(conv => conv.Id == convId).SingleOrDefaultAsync();
        }

    }
}
