package me.rumbugen.sevdesk.requests

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.Util
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.SevQuery
import me.rumbugen.sevdesk.objects.contact.customField.ContactCustomField
import me.rumbugen.sevdesk.objects.contact.customField.ContactCustomFieldPage
import me.rumbugen.sevdesk.objects.contact.customField.ContactCustomFieldSerializer
import me.rumbugen.sevdesk.objects.invoice.Invoice
import me.rumbugen.sevdesk.objects.invoice.InvoicePage
import me.rumbugen.sevdesk.objects.invoice.InvoiceSerializer
import me.rumbugen.sevdesk.objects.sevDeskFile.SevDeskFile
import me.rumbugen.sevdesk.objects.sevDeskFile.SevDeskFileSerializer

/**
 *
 */
class InvoiceRequests internal constructor(private var sevDeskAPI: SevDeskAPI) {

    /**
     * Retrieve invoices
     *
     * There are a multitude of parameter which can be used to filter. A few of them are attached but for a complete list please check out this list
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @param partiallyPaid Retrieve all invoices which are partially paid. (optional)
     * @param orderByDebit Retrieve all invoices ordered by their debit (optional)
     * @param orderByDueTime Retrieve all invoices ordered by their due time (optional)
     * @param showAll Retrieve all invoices of all types (optional)
     * @param invoiceNumber Only retrieve all invoices with number as a invoice number (for example "RE-1000") (optional)
     * @param delinquent Only retrieve delinquent (due) invoices (optional)
     * @param notdelinquent Only retrieve invoices which are not delinquent (due) (optional)
     * @param status Only retrieve invoices of a given status (optional)
     * @param createBefore Only retrieve all invoices created before timestamp (optional)
     * @param createAfter Only retrieve all invoices created after timestamp (optional)
     * @param updateBefore Only retrieve all invoices updated last before timestamp (optional)
     * @param updateAfter Only retrieve all invoices updated last after timestamp (optional)
     * @param contactId Only retrieve all invoices with id as a contact (optional)
     * @param orderByDueDate Retrieve all invoices ordered by their due date (optional)
     * @param day Only retrieve all invoices where invoice date falls on the day of timestamp (optional)
     * @param startDate Only retrieve all invoices where invoice date is bigger than timestamp (optional)
     * @param endDate Only retrieve all invoices where invoice date is smaller than timestamp
     * If startDate and endDate are both supplied, you will get all invoices in the defined range (optional)
     * @param header Only retrieve all invoices with header as a header (optional)
     * @param onlyDunned Retrieve all invoices which are dunned (optional)
     * @param showWkr Retrieve all recurring invoices (optional)
     * @param showMa Retrieve all invoices which are payment reminders (optional)
     *
     * @return [Pair] A Pair containing:
     *   - A List of [Invoice] objects.
     *   - An optional Integer representing the total number of [Invoice]'s in sevDesk if `countAll` is true, otherwise null.
     */
    suspend fun retrieveInvoices(
        countAll: Boolean = false,
        limit: Int?,
        offset: Int?,

        partiallyPaid: Boolean? = null,
        orderByDebit: Boolean? = null,
        orderByDueTime: Boolean? = null,
        showAll: Boolean? = null,
        invoiceNumber: String? = null,
        delinquent: Boolean? = null,
        notdelinquent: Boolean? = null,
        status: Invoice.Status? = null,
        createBefore: Long? = null,
        createAfter: Long? = null,
        updateBefore: Long? = null,
        updateAfter: Long? = null,
        contactId: Int? = null,
        orderByDueDate: String? = null,
        day: Long? = null,
        startDate: Long? = null,
        endDate: Long? = null,
        header: String? = null,
        onlyDunned: Boolean? = null,
        showWkr: Boolean? = null,
        showMa: Boolean? = null,
    ): Pair<List<Invoice>, Int?> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Invoice") {
                    parameter("countAll", countAll)
                    if (limit != null) parameter("limit", limit)
                    if (offset != null) parameter("offset", offset)

                    partiallyPaid?.let { parameter("partiallyPaid", partiallyPaid) }
                    orderByDebit?.let { parameter("orderByDebit", orderByDebit) }
                    orderByDueTime?.let { parameter("orderByDueTime", orderByDueTime) }
                    showAll?.let { parameter("showAll", showAll) }
                    invoiceNumber?.let { parameter("invoiceNumber", invoiceNumber) }
                    delinquent?.let { parameter("delinquent", delinquent) }
                    notdelinquent?.let { parameter("notdelinquent", notdelinquent) }
                    status?.let { parameter("status", status) }
                    createBefore?.let { parameter("createBefore", createBefore) }
                    createAfter?.let { parameter("createAfter", createAfter) }
                    updateBefore?.let { parameter("updateBefore", updateBefore) }
                    updateAfter?.let { parameter("updateAfter", updateAfter) }
                    contactId?.let {
                        parameter("contact[id]", contactId)
                        parameter("contact[objectName]", "Contact")
                    }
                    orderByDueDate?.let { parameter("orderByDueDate", orderByDueDate) }
                    day?.let { parameter("day", day) }
                    startDate?.let { parameter("startDate", startDate) }
                    endDate?.let { parameter("endDate", endDate) }
                    header?.let { parameter("header", header) }
                    onlyDunned?.let { parameter("onlyDunned", onlyDunned) }
                    showWkr?.let { parameter("showWkr", showWkr) }
                    showMa?.let { parameter("showMa", showMa) }
                }
            },
            {
                Util.getObjectList(it, InvoiceSerializer).to(it["total"]?.jsonPrimitive?.contentOrNull?.toInt())
            }) ?: mutableListOf<Invoice>().to(null)
    }

    /**
     * Retrieve invoices
     *
     * There are a multitude of parameter which can be used to filter. A few of them are attached but for a complete list please check out this list
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @param partiallyPaid Retrieve all invoices which are partially paid. (optional)
     * @param orderByDebit Retrieve all invoices ordered by their debit (optional)
     * @param orderByDueTime Retrieve all invoices ordered by their due time (optional)
     * @param showAll Retrieve all invoices of all types (optional)
     * @param invoiceNumber Only retrieve all invoices with number as a invoice number (for example "RE-1000") (optional)
     * @param delinquent Only retrieve delinquent (due) invoices (optional)
     * @param notdelinquent Only retrieve invoices which are not delinquent (due) (optional)
     * @param status Only retrieve invoices of a given status (optional)
     * @param createBefore Only retrieve all invoices created before timestamp (optional)
     * @param createAfter Only retrieve all invoices created after timestamp (optional)
     * @param updateBefore Only retrieve all invoices updated last before timestamp (optional)
     * @param updateAfter Only retrieve all invoices updated last after timestamp (optional)
     * @param contactId Only retrieve all invoices with id as a contact (optional)
     * @param orderByDueDate Retrieve all invoices ordered by their due date (optional)
     * @param day Only retrieve all invoices where invoice date falls on the day of timestamp (optional)
     * @param startDate Only retrieve all invoices where invoice date is bigger than timestamp (optional)
     * @param endDate Only retrieve all invoices where invoice date is smaller than timestamp
     * If startDate and endDate are both supplied, you will get all invoices in the defined range (optional)
     * @param header Only retrieve all invoices with header as a header (optional)
     * @param onlyDunned Retrieve all invoices which are dunned (optional)
     * @param showWkr Retrieve all recurring invoices (optional)
     * @param showMa Retrieve all invoices which are payment reminders (optional)
     *
     * @return A [List] of [Invoice] objects.
     */
    suspend fun retrieveInvoicesWithoutPagination(
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null,

        partiallyPaid: Boolean? = null,
        orderByDebit: Boolean? = null,
        orderByDueTime: Boolean? = null,
        showAll: Boolean? = null,
        invoiceNumber: String? = null,
        delinquent: Boolean? = null,
        notdelinquent: Boolean? = null,
        status: Invoice.Status? = null,
        createBefore: Long? = null,
        createAfter: Long? = null,
        updateBefore: Long? = null,
        updateAfter: Long? = null,
        contactId: Int? = null,
        orderByDueDate: String? = null,
        day: Long? = null,
        startDate: Long? = null,
        endDate: Long? = null,
        header: String? = null,
        onlyDunned: Boolean? = null,
        showWkr: Boolean? = null,
        showMa: Boolean? = null,
    ): List<Invoice> {
        return retrieveInvoices(
            countAll = countAll,
            limit = limit,
            offset = offset,
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
        ).first
    }

    /**
     * Retrieve invoices
     *
     * There are a multitude of parameter which can be used to filter. A few of them are attached but for a complete list please check out this list
     *
     * This method fetches objects, applying pagination based on the provided `perPage` and `page` values.
     * It internally uses the "withoutPagination" method to fetch the data with specified limit and offset.
     *
     * @param perPage The number of objects to retrieve per page. Defaults to 10.
     * @param page The page number to retrieve. Defaults to 1.
     *
     * @param partiallyPaid Retrieve all invoices which are partially paid. (optional)
     * @param orderByDebit Retrieve all invoices ordered by their debit (optional)
     * @param orderByDueTime Retrieve all invoices ordered by their due time (optional)
     * @param showAll Retrieve all invoices of all types (optional)
     * @param invoiceNumber Only retrieve all invoices with number as a invoice number (for example "RE-1000") (optional)
     * @param delinquent Only retrieve delinquent (due) invoices (optional)
     * @param notdelinquent Only retrieve invoices which are not delinquent (due) (optional)
     * @param status Only retrieve invoices of a given status (optional)
     * @param createBefore Only retrieve all invoices created before timestamp (optional)
     * @param createAfter Only retrieve all invoices created after timestamp (optional)
     * @param updateBefore Only retrieve all invoices updated last before timestamp (optional)
     * @param updateAfter Only retrieve all invoices updated last after timestamp (optional)
     * @param contactId Only retrieve all invoices with id as a contact (optional)
     * @param orderByDueDate Retrieve all invoices ordered by their due date (optional)
     * @param day Only retrieve all invoices where invoice date falls on the day of timestamp (optional)
     * @param startDate Only retrieve all invoices where invoice date is bigger than timestamp (optional)
     * @param endDate Only retrieve all invoices where invoice date is smaller than timestamp
     * If startDate and endDate are both supplied, you will get all invoices in the defined range (optional)
     * @param header Only retrieve all invoices with header as a header (optional)
     * @param onlyDunned Retrieve all invoices which are dunned (optional)
     * @param showWkr Retrieve all recurring invoices (optional)
     * @param showMa Retrieve all invoices which are payment reminders (optional)
     *
     * @return A [Page] object containing a mutable list of [Invoice] objects representing the check accounts for the current page.
     */
    suspend fun retrieveInvoicesWithPagination(
        perPage: Int = 10,
        page: Int = 1,

        partiallyPaid: Boolean? = null,
        orderByDebit: Boolean? = null,
        orderByDueTime: Boolean? = null,
        showAll: Boolean? = null,
        invoiceNumber: String? = null,
        delinquent: Boolean? = null,
        notdelinquent: Boolean? = null,
        status: Invoice.Status? = null,
        createBefore: Long? = null,
        createAfter: Long? = null,
        updateBefore: Long? = null,
        updateAfter: Long? = null,
        contactId: Int? = null,
        orderByDueDate: String? = null,
        day: Long? = null,
        startDate: Long? = null,
        endDate: Long? = null,
        header: String? = null,
        onlyDunned: Boolean? = null,
        showWkr: Boolean? = null,
        showMa: Boolean? = null,
    ): Page<Invoice> {
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = retrieveInvoices(
            countAll = true,
            limit = limit,
            offset = offset,
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

        return InvoicePage(
            invoiceRequests = this,

            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!,

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

    /**
     * Retrieve pdf document of an invoice
     *
     * Retrieves the pdf document of an invoice with additional metadata.
     *
     * @param invoiceId ID of invoice from which you want the pdf
     * @param preventSendBy Defines if u want to send the invoice
     */
    suspend fun retrievePDFDocumentInvoice(
        invoiceId: Int,
        preventSendBy: Boolean
    ): SevDeskFile? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Invoice/$invoiceId/getPdf") {
                    parameter("preventSendBy", preventSendBy)
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(SevDeskFileSerializer, it["objects"]!!)
            })
    }

    suspend fun markInvoiceAsSent(
        invoiceId: Int,
        sendType: Invoice.SendType,
        sendDraft: Boolean
    ): Invoice? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.put("Invoice/$invoiceId/sendBy") {
                    parameter("sendType", sendType.toString())
                    parameter("sendDraft", sendDraft)
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(InvoiceSerializer, it["objects"]!!)
            })
    }
}