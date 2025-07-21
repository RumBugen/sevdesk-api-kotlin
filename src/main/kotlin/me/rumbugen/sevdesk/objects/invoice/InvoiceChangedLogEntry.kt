package me.rumbugen.sevdesk.objects.invoice

import me.rumbugen.sevdesk.objects.SevDeskObject
import me.rumbugen.sevdesk.objects.basic.SevClient
import java.time.OffsetDateTime

// Yes, this is to 70% not documented in SevDesk, you guessed it :)
data class InvoiceChangedLogEntry(
    override val id: Int,

    override val create: OffsetDateTime? = null,
    override val update: OffsetDateTime? = null,

    // TODO: CreditNote endpoints aren't available jet

    val fromStatus: Invoice.Status? = null,
    val toStatus: Invoice.Status? = null,
    val ammountPayed: String? = null,
    val bookingDate: OffsetDateTime? = null,
    override var sevClient: SevClient? = null,
): SevDeskObject(id, create, update, sevClient)