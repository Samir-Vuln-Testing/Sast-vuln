define([], function() {

    var storedCodeExecutor = {

        storeUserData: function() {
            var userInput = frmExecutor.txtUserInput.text;
            kony.store.setItem("stored_data", userInput);

            // ✅ Just display safely (no execution)
            kony.print("Stored Data: " + userInput);
        },

        evaluateExpressionSafely: function() {
            var userExpression = frmExecutor.txtUserInput.text;

            // ❌ No eval → only allow numbers
            if (!/^[0-9+\-*/ ().]+$/.test(userExpression)) {
                kony.print("Invalid expression");
                return;
            }

            try {
                // Still controlled
                var result = Function('"use strict"; return (' + userExpression + ')')();
                kony.print(result);
            } catch (e) {
                kony.print("Error evaluating expression");
            }
        },

        storeAction: function() {
            var action = frmExecutor.txtAction.text;

            // ✅ Whitelist allowed actions
            var allowedActions = ["printHello", "printDate"];

            if (!allowedActions.includes(action)) {
                kony.print("Invalid action");
                return;
            }

            kony.store.setItem("stored_action", action);
            this.execute(action);
        },

        execute: function(action) {
            switch (action) {
                case "printHello":
                    kony.print("Hello");
                    break;
                case "printDate":
                    kony.print(new Date());
                    break;
                default:
                    kony.print("Unknown action");
            }
        },

        scheduleAction: function() {
            var delay = parseInt(frmExecutor.txtDelay.text);

            if (isNaN(delay) || delay < 0) {
                kony.print("Invalid delay");
                return;
            }

            setTimeout(function() {
                kony.print("Scheduled action executed");
            }, delay);
        },

        pollingSafe: function() {
            var interval = parseInt(frmExecutor.txtDelay.text);

            if (isNaN(interval) || interval < 0) {
                kony.print("Invalid interval");
                return;
            }

            setInterval(function() {
                kony.print("Polling...");
            }, interval);
        },

        handleServerResponse: function(response) {
            // ❌ Never execute server script
            // ✅ Just treat as data
            kony.print("Received response: " + JSON.stringify(response));
        },

        loadExternalScriptSafely: function() {
            var url = frmExecutor.txtUserInput.text;

            // ✅ Allow only trusted domains
            if (!url.startsWith("https://trusted-domain.com")) {
                kony.print("Untrusted URL blocked");
                return;
            }

            frmExecutor.wbvContent.html = "<script src='" + url + "'><\/script>";
        },

        processBatchCommands: function() {
            var rawInput = frmExecutor.txtCommands.text;

            try {
                var cmds = JSON.parse(rawInput);

                // ✅ Only log commands, do not execute
                cmds.forEach(function(cmd) {
                    kony.print("Command: " + cmd);
                });

            } catch (e) {
                kony.print("Invalid JSON");
            }
        },

        invokeSafeMethod: function() {
            var methodName = frmExecutor.txtMethodName.text;

            var allowedMethods = {
                sayHi: function() { kony.print("Hi"); },
                sayBye: function() { kony.print("Bye"); }
            };

            if (allowedMethods[methodName]) {
                allowedMethods[methodName]();
            } else {
                kony.print("Invalid method");
            }
        },

        storeProfile: function() {
            var userProfile = frmExecutor.txtUserInput.text;

            try {
                var profile = JSON.parse(userProfile);
                kony.print(profile.name || "No name");
            } catch (e) {
                kony.print("Invalid profile format");
            }
        }
    };

    return storedCodeExecutor;
});
