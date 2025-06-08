package me.rumbugen.sevdesk.objects.contact

enum class ContactNumberItems(var overrideValue: String? = null) {
    ORDERS,
    INVOICES,
    CREDIT_NOTES("creditNotes"),
    DOCUMENTS,
    PERSONS,
    VOUCHERS,
    LETTERS,
    PARTS,
    INVOICE_POS("invoicePos")
}