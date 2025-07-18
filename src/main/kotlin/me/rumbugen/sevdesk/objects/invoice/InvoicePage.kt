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
    override val total: Int?,

    val partiallyPaid: Boolean? = null,
    val orderByDebit: Boolean? = null,
    val orderByDueTime: Boolean? = null,
    val showAll: Boolean? = null,
    val invoiceNumber: String? = null,
    val delinquent: Boolean? = null,
    val notdelinquent: Boolean? = null,
    val status: Invoice.Status? = null,
    val createBefore: Long? = null,
    val createAfter: Long? = null,
    val updateBefore: Long? = null,
    val updateAfter: Long? = null,
    val contactId: Int? = null,
    val orderByDueDate: String? = null,
    val day: Long? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val header: String? = null,
    val onlyDunned: Boolean? = null,
    val showWkr: Boolean? = null,
    val showMa: Boolean? = null,
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
            total = checkAccounts.second!!,

            partiallyPaid = partiallyPaid,
            orderByDebit = orderByDebit,
            orderByDueTime = orderByDueTime,
            showAll = showAll,
            invoiceNumber = invoiceNumber,
            delinquent = delinquent,
            notdelinquent = notdelinquent,
            status = status,
            createBefore = createBefore,
            createAfter = createAfter,
            updateBefore = updateBefore,
            updateAfter = updateAfter,
            contactId = contactId,
            orderByDueDate = orderByDueDate,
            day = day,
            startDate = startDate,
            endDate = endDate,
            header = header,
            onlyDunned = onlyDunned,
            showWkr = showWkr,
            showMa = showMa
        )
    }
}