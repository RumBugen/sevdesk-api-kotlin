package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import me.rumbugen.sevdesk.objects.SevQuery
import me.rumbugen.sevdesk.objects.sevDeskFile.SevDeskFile
import me.rumbugen.sevdesk.tests.TestObjects.sevDeskAPI
import me.rumbugen.sevdesk.tests.TestObjects.testSevDeskFile
import org.junit.jupiter.api.Test

class TestExportRequest {
    val requests = sevDeskAPI.exportRequest()

    @Test
    fun exportInvoice() = runBlocking {
        val sevDeskFile = requests.exportInvoice(
            false, SevQuery(
                limit = 1
            )
        )

        testSevDeskFile(sevDeskFile)
        return@runBlocking
    }


    @Test
    fun exportInvoiceZip() = runBlocking {
        val sevDeskFile = requests.exportInvoiceAsZIP(
            false, SevQuery(
                limit = 1
            )
        )

        testSevDeskFile(sevDeskFile)
        return@runBlocking
    }

}