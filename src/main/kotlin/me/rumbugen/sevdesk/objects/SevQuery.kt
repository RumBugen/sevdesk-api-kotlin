package me.rumbugen.sevdesk.objects

import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.invoice.InvoiceType
import java.time.OffsetDateTime

data class SevQuery(
    val limit: Int? = null,
    val filterInvoiceType: InvoiceType? = null,
    val filterStartDate: OffsetDateTime? = null,
    val filterEndDate: OffsetDateTime? = null,
    val filterContact: Contact? = null,
    val filterStartAmount: Int? = null,
    val filterEndAmount: Int? = null,
) {
}