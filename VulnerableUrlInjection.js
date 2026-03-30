function sendUserRequest(frmMain) {
    var userInput = frmMain.txtUrl.text;

    var httpClient = new kony.net.HttpRequest();
    httpClient.open("GET", userInput);
    httpClient.send();
}