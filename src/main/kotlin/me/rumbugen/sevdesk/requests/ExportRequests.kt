package me.rumbugen.sevdesk.requests

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.Json
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.objects.SevQuery
import me.rumbugen.sevdesk.objects.sevDeskFile.SevDeskFile
import me.rumbugen.sevdesk.objects.sevDeskFile.SevDeskFileSerializer

/**
 * Export
 *
 * A set of operations to export data.
 */
class ExportRequests internal constructor(private var sevDeskAPI: SevDeskAPI) {

    /**
     * Export invoice
     *
     * Export all invoices as csv
     */
    suspend fun exportInvoice(
        download: Boolean = false,
        sevQuery: SevQuery
    ): SevDeskFile? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Export/invoiceCsv") {
                    sevQuery(download, sevQuery)
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(SevDeskFileSerializer, it["objects"]!!)
            })
    }

    /**
     * Export Invoice as zip
     *
     * Export all invoices as zip
     */
    suspend fun exportInvoiceAsZIP(
        download: Boolean?,
        sevQuery: SevQuery
    ): SevDeskFile? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Export/invoiceZip") {
                    sevQuery(download, sevQuery)
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(SevDeskFileSerializer, it["objects"]!!)
            })
    }

    private fun HttpRequestBuilder.sevQuery(
        download: Boolean?,
        sevQuery: SevQuery
    ) {
        if (download != false) parameter("download", true)
        parameter("sevQuery[modelName]", "Invoice")
        parameter("sevQuery[objectName]", "SevQuery")
        sevQuery.limit?.let { parameter("sevQuery[limit]", it) }
        sevQuery.filterInvoiceType?.let { parameter("sevQuery[filter][invoiceType]", it.name) }
        sevQuery.filterStartDate?.let { parameter("sevQuery[filter][startDate]", it.toString()) }
        sevQuery.filterEndDate?.let { parameter("sevQuery[filter][endDate]", it.toString()) }
        sevQuery.filterContact?.let {
            parameter("sevQuery[filter][contact][id]", it.id)
            parameter("sevQuery[filter][contact][objectName]", it.objectName)
        }
        sevQuery.filterStartAmount?.let { parameter("sevQuery[filter][startAmount]", it) }
        sevQuery.filterEndAmount?.let { parameter("sevQuery[filter][endAmount]", it) }
    }
}