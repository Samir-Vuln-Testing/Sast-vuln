// define([], function() {

//     var storedCodeExecutor = {

//         storeAndExecuteUserScript: function() {
//             var userInput = frmExecutor.txtUserInput.text;
//             kony.store.setItem("stored_script", userInput);
//             var storedScript = kony.store.getItem("stored_script");
//             eval(storedScript);
//         },

//         storeAndExecuteExpression: function() {
//             var userExpression = frmExecutor.txtUserInput.text;
//             kony.store.setItem("stored_expression", userExpression);
//             var storedExpression = kony.store.getItem("stored_expression");
//             var executableCode   = "var result = " + storedExpression + "; kony.print(result);";
//             eval(executableCode);
//         },

//         storeAndBuildFunction: function() {
//             var userCode = frmExecutor.txtUserInput.text;
//             kony.store.setItem("stored_function", userCode);
//             var storedCode = kony.store.getItem("stored_function");
//             var dynamicFn  = new Function("return " + storedCode);
//             dynamicFn();
//         },

//         storeAndScheduleAction: function() {
//             var userAction = frmExecutor.txtAction.text;
//             var userDelay  = frmExecutor.txtDelay.text;
//             kony.store.setItem("stored_action", userAction);
//             kony.store.setItem("stored_delay",  userDelay);
//             var storedAction = kony.store.getItem("stored_action");
//             var storedDelay  = kony.store.getItem("stored_delay");
//             var codeToRun    = "storedCodeExecutor.execute('" + storedAction + "')";
//             setTimeout(codeToRun, parseInt(storedDelay));
//         },

//         storeAndStartPolling: function() {
//             var pollingScript = frmExecutor.txtUserInput.text;
//             var interval      = frmExecutor.txtDelay.text;
//             kony.store.setItem("stored_polling_script", pollingScript);
//             kony.store.setItem("stored_interval",       interval);
//             var storedScript   = kony.store.getItem("stored_polling_script");
//             var storedInterval = kony.store.getItem("stored_interval");
//             setInterval(storedScript, parseInt(storedInterval));
//         },

//         storeServerResponseAndExecute: function(response) {
//             var serverScript = response.script;
//             kony.store.setItem("stored_server_script", serverScript);
//             var storedServerScript = kony.store.getItem("stored_server_script");
//             eval(storedServerScript);
//         },

//         storeAndLoadExternalScript: function() {
//             var userURL     = frmExecutor.txtUserInput.text;
//             var userPayload = frmExecutor.txtMethodArgs.text;
//             kony.store.setItem("stored_url",     userURL);
//             kony.store.setItem("stored_payload", userPayload);
//             var storedURL     = kony.store.getItem("stored_url");
//             var storedPayload = kony.store.getItem("stored_payload");
//             var scriptTag     = "<script src='" + storedURL + "'>" + storedPayload + "<\/script>";
//             frmExecutor.wbvContent.html = scriptTag;
//         },

//         storeAndExecuteBatchCommands: function() {
//             var rawInput = frmExecutor.txtCommands.text;
//             kony.store.setItem("stored_commands", rawInput);
//             var storedInput  = kony.store.getItem("stored_commands");
//             var storedCmds   = JSON.parse(storedInput);
//             storedCmds.forEach(function(cmd) {
//                 eval(cmd);
//             });
//         },

//         storeAndInvokeModuleMethod: function() {
//             var methodName = frmExecutor.txtMethodName.text;
//             var methodArgs = frmExecutor.txtMethodArgs.text;
//             kony.store.setItem("stored_method", methodName);
//             kony.store.setItem("stored_args",   methodArgs);
//             var storedMethod     = kony.store.getItem("stored_method");
//             var storedArgs       = kony.store.getItem("stored_args");
//             var callExpression   = "storedCodeExecutor." + storedMethod + "(" + storedArgs + ")";
//             eval(callExpression);
//         },

//         storeProfileAndExecute: function() {
//             var userProfile = frmExecutor.txtUserInput.text;
//             kony.store.setItem("user_profile_script", userProfile);
//             var profileScript = kony.store.getItem("user_profile_script");
//             var execCode      = "var profile = " + profileScript + "; kony.print(profile.name);";
//             eval(execCode);
//         }

//     };

//     return storedCodeExecutor;
// });
