package me.rumbugen.sevdesk.objects.invoice

enum class InvoiceType {
    /**
     * Normal invoice
     */
    RE,

    /**
     * Recurring invoice
     */
    WKR,

    /**
     * Cancellation invoice
     */
    SR,

    /**
     * Reminder invoice
     */
    MA,

    /**
     * Partial invoice
     */
    TR,

    /**
     * Advance invoice
     */
    AR,

    /**
     * Final invoice
     */
    ER
}