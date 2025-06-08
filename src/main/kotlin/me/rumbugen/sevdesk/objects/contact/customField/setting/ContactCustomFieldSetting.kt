package me.rumbugen.sevdesk.objects.contact.customField.setting

import me.rumbugen.sevdesk.objects.SevDeskObject
import me.rumbugen.sevdesk.objects.basic.SevClient
import me.rumbugen.sevdesk.objects.contact.Contact
import java.time.OffsetDateTime

data class ContactCustomFieldSetting(
    /**
     * id of the contact field
     */
    override val id: Int,

    /**
     * Date of contact field creation
     */
    override val create: OffsetDateTime? = null,

    /**
     * Date of contact field update
     */
    override val update: OffsetDateTime? = null,

    /**
     * Client to which contact field setting belongs.
     */
    override var sevClient: SevClient? = null,

    /**
     * name of the contact fields
     */
    var name: String? = null,

    /**
     * Unique identifier for the contact field
     */
    var identifier: String? = null,

    /**
     * The description of the contact field
     */
    var description: String? = null
): SevDeskObject(id, create, update, sevClient)