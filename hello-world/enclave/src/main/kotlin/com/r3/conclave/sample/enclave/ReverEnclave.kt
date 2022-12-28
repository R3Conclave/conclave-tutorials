package com.r3.conclave.sample.enclave

/**
 * Simply reverses the bytes that are passed in.
 */
class ReverseEnclave : com.r3.conclave.enclave.Enclave() {
    // We store the previous result to showcase that the enclave internals can be examined in a mock test.
    lateinit var previousResult: ByteArray
    protected override fun receiveFromUntrustedHost(bytes: ByteArray): ByteArray? {
        // This is used for host->enclave calls so we don't have to think about authentication.
        val input = String(bytes)
        val result: ByteArray = reverse(input).toByteArray()
        previousResult = result
        println("Hello. Stdout")
        java.lang.System.err.println("Hello. Stderr")
        return result
    }

    @kotlin.Throws(java.lang.Exception::class)
    protected override fun receiveMail(mail: com.r3.conclave.mail.EnclaveMail, routingHint: String?) {
        // This is used when the host delivers a message from the client.
        // First, decode mail body as a String.
        val stringToReverse = String(mail.getBodyAsBytes())
        // Reverse it and re-encode to UTF-8 to send back.
        val reversedEncodedString: ByteArray = reverse(stringToReverse).toByteArray()
        // Get the post office object for responding back to this mail and use it to encrypt our response.
        val responseBytes: ByteArray = postOffice(mail).encryptMail(reversedEncodedString)
        postMail(responseBytes, routingHint)
    }

    companion object {
        @kotlin.Throws(java.lang.Exception::class)
        private fun reverse(input: String): String {
            if (true) {
                throw java.lang.Exception("HEY")
            }
            println("Hello. Stdout")
            java.lang.System.err.println("Hello. Stderr")
            val builder = StringBuilder(input.length)
            for (i in input.length - 1 downTo 0) builder.append(input[i])
            return builder.toString()
        }
    }
}