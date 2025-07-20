package me.rumbugen.sevdesk.tests

import kotlinx.coroutines.runBlocking
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.basic.BookingVersion
import me.rumbugen.sevdesk.objects.communicationWay.CommunicationWay
import me.rumbugen.sevdesk.objects.invoice.Invoice
import me.rumbugen.sevdesk.objects.sevDeskFile.SevDeskFile
import me.rumbugen.sevdesk.requests.InvoiceRequests
import me.rumbugen.sevdesk.tests.TestObjects.testSevDeskFile
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class TestInvoiceRequests {
    val invoiceRequests = TestObjects.sevDeskAPI.invoiceRequest()

    // TODO: Currently you need manually create 2 Invoices per API Account, yeah I know its bad, but we have no endpoint currently implemented here

    @Test
    fun testInvoiceRequests() = runBlocking {
        val pagination: Page<Invoice> =
            invoiceRequests.retrieveInvoicesWithPagination(perPage = 1)
        assert(pagination.items.size == 1)

        val next: Page<Invoice> = pagination.next()
        assert(next.items.size == 1)
        assert(next.previous().items.size == 1)
    }

    @Test
    fun testFindInvoiceById() = runBlocking {
        val pagination: Page<Invoice> =
            invoiceRequests.retrieveInvoicesWithPagination(perPage = 1)
        assert(pagination.items.size == 1)

        val invoice: Invoice? = invoiceRequests.findInvoiceById(pagination.items[0].id)
        assert(invoice?.id == pagination.items[0].id)
    }

    @Test
    fun retrievePDFDocumentInvoice() = runBlocking {
        val invoices: List<Invoice> =
            invoiceRequests.retrieveInvoicesWithoutPagination(limit = 1)
        assert(invoices.isNotEmpty())

        val file = invoiceRequests.retrievePDFDocumentInvoice(invoices[0].id, true)
        testSevDeskFile(file)
    }

    @Test
    fun markInvoiceAsSent() = runBlocking {
        val invoices: List<Invoice> =
            invoiceRequests.retrieveInvoicesWithoutPagination(limit = 1)
        assert(invoices.isNotEmpty())

        val invoice: Invoice? = invoiceRequests.markInvoiceAsSent(invoices[0].id, Invoice.SendType.VM, false)
        assertNotNull(invoice)
        return@runBlocking
    }
}