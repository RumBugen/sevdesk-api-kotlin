package me.rumbugen.sevdesk.objects.contact.address

import me.rumbugen.sevdesk.objects.SevDeskObject
import me.rumbugen.sevdesk.objects.basic.SevClient
import me.rumbugen.sevdesk.objects.basic.Category
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountry
import java.time.LocalDate
import java.time.OffsetDateTime

data class ContactAddress(
    /**
     * The contact address id
     */
    override val id: Int,

    /**
     * Date of contact address creation
     */
    override val create: OffsetDateTime? = null,

    /**
     * Date of last contact address update
     */
    override val update: OffsetDateTime? = null,

    /**
     * The contact to which this contact address belongs.
     */
    var contact: Contact? = null,

    /**
     * Street name
     */
    var street: String? = null,

    /**
     * Zip code
     */
    var zip: String? = null,

    /**
     * City name
     */
    var city: String? = null,

    /**
     * Country of the contact address.
     */
    var country: StaticCountry? = null,

    /**
     * Category of the contact address
     */
    var category: Category? = null,

    /**
     * Client to which contact address belongs. Will be filled automatically
     */
    override var sevClient: SevClient? = null,

    /**
     * Name in address
     */
    var name: String? = null,
) : SevDeskObject(id, create, update, sevClient)