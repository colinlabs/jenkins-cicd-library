package io.colin.piper

def decodeBase64(String content) {
    return new String(content.trim().decodeBase64())
}

def encodeBase64(String content) {
    return content.bytes.encodeBase64().toString()
}