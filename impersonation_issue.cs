using System;
using System.Runtime.InteropServices;
using System.Security.Principal;
using Microsoft.AspNetCore.Mvc;

namespace VulnerableApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ImpersonationController1 : ControllerBase
    {
        [DllImport("advapi32.dll", SetLastError = true)]
        public static extern bool LogonUser(
            string lpszUsername,
            string lpszDomain,
            string lpszPassword,
            int dwLogonType,
            int dwLogonProvider,
            out IntPtr phToken);

        [HttpPost("login")]
        public IActionResult ImpersonateUser([FromBody] LoginRequest request)
        {
            IntPtr token = IntPtr.Zero;
            
            bool success = LogonUser(
                request.Username,  
                request.Domain,    
                request.Password,  
                2, 
                0, 
                out token);

            if (success)
            {
                WindowsIdentity identity = new WindowsIdentity(token);
                WindowsImpersonationContext context = identity.Impersonate();
                return Ok("Impersonation successful");
            }

            return Unauthorized();
        }
    }

    public class LoginRequest
    {
        public string Username { get; set; }
        public string Domain { get; set; }
        public string Password { get; set; }
    }
}
