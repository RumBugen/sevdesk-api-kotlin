package me.rumbugen.sevdesk.objects.invoice

import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.Sorting
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.requests.ContactRequests
import me.rumbugen.sevdesk.requests.InvoiceRequests
import java.time.OffsetDateTime

class InvoicePage(
    val invoiceRequests: InvoiceRequests,

    override val items: List<Invoice>,
    override val limit: Int,
    override val offset: Int,
    override val total: Int?
) : Page<Invoice>(items, limit, offset, total) {
    override suspend fun next(): Page<Invoice> {
        require(hasNext()) { "No next page available" }

        val newOffset = limit * currentPage
        return makeRequest(newOffset)
    }

    override suspend fun previous(): Page<Invoice> {
        require(hasPrevious()) { "No previous page available" }

        val newOffset = limit * (currentPage - 1)
        return makeRequest(newOffset)
    }

    private suspend fun makeRequest(newOffset: Int): Page<Invoice> {
        val checkAccounts =
            invoiceRequests.retrieveInvoices(
                countAll = true,
                limit = limit,
                offset = newOffset
            )
        return InvoicePage(
            invoiceRequests = invoiceRequests,

            items = checkAccounts.first,
            limit = limit,
            offset = newOffset,
            total = checkAccounts.second!!
        )
    }
}