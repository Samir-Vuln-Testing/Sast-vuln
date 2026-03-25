using System;
using System.Runtime.InteropServices;
using System.Security.Principal;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace VulnerableApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ImpersonationController1 : ControllerBase
    {
        private readonly ILogger<ImpersonationController1> _logger;

        public ImpersonationController1(ILogger<ImpersonationController1> logger)
        {
            _logger = logger;
        }

        [DllImport("advapi32.dll", SetLastError = true)]
        private static extern bool LogonUser(
            string username,
            string domain,
            string password,
            int logonType,
            int logonProvider,
            out IntPtr token);

        [HttpPost("login")]
        public IActionResult ImpersonateUser([FromBody] LoginRequest request)
        {
            // ❌ Do not trust raw input blindly
            if (string.IsNullOrWhiteSpace(request.Username) ||
                string.IsNullOrWhiteSpace(request.Domain) ||
                string.IsNullOrWhiteSpace(request.Password))
            {
                return BadRequest("Invalid input");
            }

            IntPtr token = IntPtr.Zero;

            try
            {
                bool success = LogonUser(
                    request.Username,
                    request.Domain,
                    request.Password,
                    2, // LOGON32_LOGON_INTERACTIVE
                    0,
                    out token);

                if (!success)
                {
                    _logger.LogWarning("Impersonation failed for user {User}", request.Username);
                    return Unauthorized();
                }

                using (WindowsIdentity identity = new WindowsIdentity(token))
                {
                    using (WindowsImpersonationContext context = identity.Impersonate())
                    {
                        // ✅ Do only minimal required work here
                        return Ok("Impersonation successful");
                    }
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error during impersonation");
                return StatusCode(500, "Internal server error");
            }
            finally
            {
                if (token != IntPtr.Zero)
                {
                    CloseHandle(token); // ✅ prevent leak
                }
            }
        }

        [DllImport("kernel32.dll")]
        private static extern bool CloseHandle(IntPtr handle);
    }

    public class LoginRequest
    {
        public string Username { get; set; }
        public string Domain { get; set; }

        // ⚠️ Ideally avoid sending password at all
        public string Password { get; set; }
    }
}
