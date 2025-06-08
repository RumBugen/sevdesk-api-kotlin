package me.rumbugen.sevdesk.objects.contact

import me.rumbugen.sevdesk.objects.SevDeskObject
import me.rumbugen.sevdesk.objects.basic.SevClient
import me.rumbugen.sevdesk.objects.basic.Category
import java.time.LocalDate
import java.time.OffsetDateTime

data class Contact(
    /**
     * The contact id
     */
    override val id: Int,

    /**
     * The organization name.
     * Be aware that the type of contact will depend on this attribute.
     * If it holds a value, the contact will be regarded as an organization.
     */
    var name: String? = null,

    /**
     * Date of contact creation
     */
    override var create: OffsetDateTime? = null,

    /**
     * Date of last contact update
     */
    override var update: OffsetDateTime? = null,

    /**
     * Defines the status of the contact.
     */
    var status: Status? = null,

    /**
     * The customer number
     */
    var customerNumber: String? = null,

    /**
     * The parent contact to which this contact belongs. Must be an organization.
     */
    var parent: Contact? = null,

    /**
     * The first name of the contact.
     * Yeah... not quite right in literally every way. We know.
     * Not to be used for organizations.
     */
    var surename: String? = null,

    /**
     * The last name of the contact.
     * Not to be used for organizations.
     */
    var familyname: String? = null,

    /**
     * A non-academic title for the contact. Not to be used for organizations.
     */
    var titel: String? = null,

    /**
     * Category of the contact.
     */
    var category: Category? = null,

    /**
     * A description for the contact.
     */
    var description: String? = null,

    /**
     * A academic title for the contact. Not to be used for organizations.
     */
    var academicTitle: String? = null,

    /**
     * Gender of the contact.
     * Not to be used for organizations.
     */
    var gender: String? = null,

    /**
     * Client to which contact belongs.
     */
    override var sevClient: SevClient? = null,

    /**
     * Second name of the contact.
     * Not to be used for organizations.
     */
    var name2: String? = null,

    /**
     * Birthday of the contact.
     * Not to be used for organizations.
     */
    var birthday: LocalDate? = null,

    /**
     * Vat number of the contact.
     */
    var vatNumber: String? = null,

    /**
     * Bank account number (IBAN) of the contact.
     */
    var bankAccount: String? = null,

    /**
     * Bank number of the bank used by the contact.
     */
    var bankNumber: String? = null,

    /**
     * Absolute time in days which the contact has to pay his invoices and subsequently get a cashback.
     */
    var defaultCashbackTime: Int? = null,

    /**
     * Percentage of the invoice sum the contact gets back if he paid invoices in time.
     */
    var defaultCashbackPercent: Float? = null,

    /**
     * The payment goal in days which is set for every invoice of the contact.
     */
    var defaultTimeToPay: Int? = null,

    /**
     * The tax number of the contact.
     */
    var taxNumber: String? = null,

    /**
     * The tax office of the contact (only for greek customers).
     */
    var taxOffice: String? = null,

    /**
     * Defines if the contact is freed from paying vat.
     */
    var exemptVat: Boolean? = null,

    /**
     * The default discount the contact gets for every invoice.
     * Depending on defaultDiscountPercentage attribute, in percent or absolute value.
     */
    var defaultDiscountAmount: Float? = null,

    /**
     * Defines if the discount is a percentage (true) or an absolute value (false).
     */
    var defaultDiscountPercentage: Boolean? = null,

    /**
     * Buyer reference of the contact.
     */
    var buyerReference: String? = null,

    /**
     * Defines whether the contact is a government agency (true) or not (false).
     */
    var governmentAgency: Boolean? = null,
): SevDeskObject(id, create, update, sevClient) {
    enum class Status(val idRepresentation: Int) {
        LEAD(100),
        PENDING(500),
        ACTIVE(1000);

        companion object {
            fun valueOf(id: Int): Status? {
                return entries.firstOrNull { it.idRepresentation == id }
            }
        }
    }
}