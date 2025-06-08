package me.rumbugen.sevdesk.objects.contact.customField

import me.rumbugen.sevdesk.objects.SevDeskObject
import me.rumbugen.sevdesk.objects.basic.SevClient
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.contact.customField.setting.ContactCustomFieldSetting
import java.time.OffsetDateTime

data class ContactCustomField(
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
     * Client to which contact field belongs.
     */
    override var sevClient: SevClient? = null,

    /**
     * name of the contact
     */
    var contact: Contact? = null,

    /**
     * the contact custom field setting
     */
    var contactCustomFieldSetting: ContactCustomFieldSetting? = null,

    /**
     * The value of the contact field
     */
    var value: String? = null
): SevDeskObject(id, create, update, sevClient)