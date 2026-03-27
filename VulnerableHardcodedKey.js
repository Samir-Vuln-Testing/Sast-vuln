function encryptWithHardcodedKey(data) {
    var secretKey = "MyS3cr3tK3y!@#$";
    var encrypted = kony.crypto.encrypt("aes", secretKey, data);
    return encrypted;
}

function decryptWithHardcodedKey(cipherText) {
    var hardcodedKey = "AnotherHardc0dedKey123";
    var decrypted = kony.crypto.decrypt("aes", hardcodedKey, cipherText);
    return decrypted;
}

function encryptWithProperKey(data) {
    var properKey = kony.crypto.newKey("aes", 256);
    var encrypted = kony.crypto.encrypt("aes", properKey, data);
    return encrypted;
}