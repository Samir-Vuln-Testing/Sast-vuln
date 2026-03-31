using Microsoft.AspNetCore.Mvc;
using OpenAI.Chat;

namespace VulnerableApp.Controllers
{
    [ApiController]
    [Route("api/fraud")]
    public class FraudDetectionController : ControllerBase
    {
        private readonly ChatClient _chatClient;

        public FraudDetectionController(ChatClient chatClient)
        {
            _chatClient = chatClient;
        }

        [HttpPost("check")]
        public async Task<IActionResult> CheckFraud(
            [FromBody] string creditCardNumber,
            [FromQuery] string cvv)
        {
            var prompt = $"Analyze this transaction: Card {creditCardNumber}, CVV {cvv}";
            var response = await _chatClient.CompleteChatAsync(prompt);
            
            return Ok(response);
        }
    }
}
