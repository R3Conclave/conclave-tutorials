package com.r3.conclave.python.host

import com.r3.conclave.host.AttestationParameters.DCAP
import com.r3.conclave.host.EnclaveHost
import com.r3.conclave.host.MailCommand
import com.r3.conclave.host.MailCommand.PostMail
import com.r3.conclave.mail.MailDecryptionException
import com.r3.conclave.mail.PostOffice
import jep.JepConfig
import jep.SharedInterpreter
import java.io.ByteArrayOutputStream


object PythonHost {

    private val mailCommands: List<MailCommand> = ArrayList()
    private var postedMail: ByteArray? = null
    private val enclaveOutput = ByteArrayOutputStream()

    @JvmStatic
    fun main(args: Array<String>) {
        SharedInterpreter.setConfig(JepConfig().apply {
            redirectStdout(enclaveOutput)
        })



        val enclaveHost = EnclaveHost.load()
        var resp = Any()
        enclaveHost.start(null, null, null) {
            postedMail = it.filterIsInstance<PostMail>().single().encryptedBytes
        }

        reverseNumber(enclaveHost)
        enclaveHost.close()
    }

    private fun reverseNumber(enclaveHost: EnclaveHost) {
        val postOffice: PostOffice = enclaveHost.enclaveInstanceInfo.createPostOffice()
        val encryptedBytes = postOffice.encryptMail("123456".toByteArray())
        enclaveHost.deliverMail(encryptedBytes, "test")

    }
}
