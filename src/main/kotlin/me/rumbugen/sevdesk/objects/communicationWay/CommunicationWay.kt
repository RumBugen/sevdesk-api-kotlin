package me.rumbugen.sevdesk.objects.communicationWay

import me.rumbugen.sevdesk.objects.SevDeskObject
import me.rumbugen.sevdesk.objects.basic.SevClient
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.communicationWay.key.CommunicationWayKey
import java.time.OffsetDateTime

data class CommunicationWay(
    /**
     * The communication way id
     */
    override val id: Int,

    /**
     * Date of communication way creation
     */
    override val create: OffsetDateTime? = null,

    /**
     * Date of last communication way update
     */
    override val update: OffsetDateTime? = null,

    /**
     * The contact to which this communication way belongs.
     */
    val contact: Contact? = null,

    /**
     * Type of the communication way
     */
    var type: Type? = null,

    /**
     * The value of the communication way.
     * For example the phone number, e-mail address or website.
     */
    var value: String? = null,

    /**
     * The key of the communication way.
     * Similar to the category of addresses.
     */
    val key: CommunicationWayKey? = null,

    /**
     * Defines whether the communication way is the main communication way for the contact.
     */
    var main: Boolean? = null,

    /**
     * Client to which communication way key belongs. Will be filled automatically
     */
    override var sevClient: SevClient? = null
): SevDeskObject(id, create, update, sevClient) {
    enum class Type {
        PHONE, EMAIL, WEB, MOBILE
    }
}