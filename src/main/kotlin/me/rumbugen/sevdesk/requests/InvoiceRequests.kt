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
     * @return [Pair] A Pair containing:
     *   - A List of [Invoice] objects.
     *   - An optional Integer representing the total number of [Invoice]'s in sevDesk if `countAll` is true, otherwise null.
     */
    suspend fun retrieveInvoices(
        countAll: Boolean = false,
        limit: Int?,
        offset: Int?
    ): Pair<List<Invoice>, Int?> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Invoice") {
                    parameter("countAll", countAll)
                    if (limit != null) parameter("limit", limit)
                    if (offset != null) parameter("offset", offset)
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
     * @return A [List] of [Invoice] objects.
     */
    suspend fun retrieveInvoicesWithoutPagination(
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null
    ): List<Invoice> {
        return retrieveInvoices(countAll, limit, offset).first
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
     * @return A [Page] object containing a mutable list of [Invoice] objects representing the check accounts for the current page.
     */
    suspend fun retrieveInvoicesWithPagination(
        perPage: Int = 10,
        page: Int = 1
    ): Page<Invoice> {
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = retrieveInvoices(true, limit, offset)

        return InvoicePage(
            invoiceRequests = this,

            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!
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