package me.rumbugen.sevdesk.requests

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.Util
import me.rumbugen.sevdesk.Util.putSerializedIfExists
import me.rumbugen.sevdesk.Util.setJSONObjectBody
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.contact.ContactSerializer
import me.rumbugen.sevdesk.objects.contact.customField.ContactCustomField
import me.rumbugen.sevdesk.objects.contact.customField.ContactCustomFieldPage
import me.rumbugen.sevdesk.objects.contact.customField.ContactCustomFieldSerializer
import me.rumbugen.sevdesk.objects.contact.customField.setting.ContactCustomFieldSetting
import me.rumbugen.sevdesk.objects.contact.customField.setting.ContactCustomFieldSettingPage
import me.rumbugen.sevdesk.objects.contact.customField.setting.ContactCustomFieldSettingSerializer

/**
 * ContactField
 *
 * The contact fields are placeholders that can be titled and filled per contact. The contact fields can then be used in invoices, credit notes and emails.
 */
class ContactFieldRequests internal constructor(private var sevDeskAPI: SevDeskAPI) {

    /**
     * Retrieve contact fields
     *
     * Retrieve all contact fields
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return [Pair] A Pair containing:
     *   - A List of [ContactCustomField] objects.
     *   - An optional Integer representing the total number of [ContactCustomField]'s in sevDesk if `countAll` is true, otherwise null.
     */
    suspend fun retrieveContactFields(
        countAll: Boolean = false,
        limit: Int?,
        offset: Int?
    ): Pair<List<ContactCustomField>, Int?> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("ContactCustomField") {
                    parameter("countAll", countAll)
                    if (limit != null) parameter("limit", limit)
                    if (offset != null) parameter("offset", offset)
                }
            },
            {
                Util.getObjectList(it, ContactCustomFieldSerializer).to(it["total"]?.jsonPrimitive?.contentOrNull?.toInt())
            }) ?: mutableListOf<ContactCustomField>().to(null)
    }

    /**
     * Retrieve contact fields
     *
     * Retrieve all contact fields
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return A [List] of [ContactCustomField] objects.
     */
    suspend fun retrieveContactFieldsWithoutPagination(
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null
    ): List<ContactCustomField> {
        return retrieveContactFields(countAll, limit, offset).first
    }

    /**
     * Retrieve contact fields
     *
     * Retrieve all contact fields
     *
     * This method fetches objects, applying pagination based on the provided `perPage` and `page` values.
     * It internally uses the "withoutPagination" method to fetch the data with specified limit and offset.
     *
     * @param perPage The number of objects to retrieve per page. Defaults to 10.
     * @param page The page number to retrieve. Defaults to 1.
     *
     * @return A [Page] object containing a mutable list of [ContactCustomField] objects representing the check accounts for the current page.
     */
    suspend fun retrieveContactFieldsWithPagination(
        perPage: Int = 10,
        page: Int = 1
    ): Page<ContactCustomField> {
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = retrieveContactFields(true, limit, offset)

        return ContactCustomFieldPage(
            contactAddressRequests = this,

            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!
        )
    }

    /**
     * Create contact field
     *
     * @param contact name of the contact
     * @param contactCustomFieldSetting name of the contact custom field setting
     * @param value The value of the contact field
     */
    suspend fun createContactField(
        contact: Contact,
        contactCustomFieldSetting: ContactCustomFieldSetting,
        value: String
    ): ContactCustomField? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.post("ContactCustomField") {
                    setJSONObjectBody(buildJsonObject {
                        putSerializedIfExists("contact", ContactSerializer, contact)
                        putSerializedIfExists("contactCustomFieldSetting", ContactCustomFieldSettingSerializer, contactCustomFieldSetting)
                        put("value", value)
                        put("objectName", "ContactCustomField")
                    })
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(ContactCustomFieldSerializer, it["objects"]!!)
            }
        )
    }

    /**
     * Retrieve contact field by id
     *
     * @param contactCustomFieldId id of the contact field
     */
    suspend fun findContactFieldByID(
        contactCustomFieldId: Int
    ): ContactCustomField? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("ContactCustomField/${contactCustomFieldId}")
            },
            {
                return@requestWithHandling Util.getObjectFromObjectList(it, ContactCustomFieldSerializer)
            }
        )
    }

    /**
     * Update a contact field
     */
    suspend fun updateContactField(
        contactCustomFieldId: Int,
        contact: Contact? = null,
        contactCustomFieldSetting: ContactCustomFieldSetting? = null,
        value: String? = null
    ): ContactCustomField? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.put("ContactCustomField/${contactCustomFieldId}") {
                    setJSONObjectBody(buildJsonObject {
                        putSerializedIfExists("contact", ContactSerializer, contact)
                        putSerializedIfExists("contactCustomFieldSetting", ContactCustomFieldSettingSerializer, contactCustomFieldSetting)
                        put("value", value)
                        put("objectName", "ContactCustomField")
                    })
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(ContactCustomFieldSerializer, it["objects"]!!)
            }
        )
    }

    /**
     * Delete a contact field
     */
    suspend fun deleteContactField(
        contactCustomFieldId: Int
    ) {
        sevDeskAPI.request {
            sevDeskAPI.client.delete("ContactCustomField/${contactCustomFieldId}")
        }
    }

    /**
     * Retrieve contact field settings
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return [Pair] A Pair containing:
     *   - A List of [ContactCustomFieldSetting] objects.
     *   - An optional Integer representing the total number of [ContactCustomFieldSetting]'s in sevDesk if `countAll` is true, otherwise null.
     */
    suspend fun retrieveContactFieldSettings(
        countAll: Boolean = false,
        limit: Int?,
        offset: Int?
    ): Pair<List<ContactCustomFieldSetting>, Int?> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("ContactCustomFieldSetting") {
                    parameter("countAll", countAll)
                    if (limit != null) parameter("limit", limit)
                    if (offset != null) parameter("offset", offset)
                }
            },
            {
                Util.getObjectList(it, ContactCustomFieldSettingSerializer).to(it["total"]?.jsonPrimitive?.contentOrNull?.toInt())
            }) ?: mutableListOf<ContactCustomFieldSetting>().to(null)
    }

    /**
     * Retrieve contact field settings
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return A [List] of [ContactCustomFieldSetting] objects.
     */
    suspend fun retrieveContactFieldSettingsWithoutPagination(
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null
    ): List<ContactCustomFieldSetting> {
        return retrieveContactFieldSettings(countAll, limit, offset).first
    }

    /**
     * Retrieve contact field settings
     *
     * This method fetches objects, applying pagination based on the provided `perPage` and `page` values.
     * It internally uses the "withoutPagination" method to fetch the data with specified limit and offset.
     *
     * @param perPage The number of objects to retrieve per page. Defaults to 10.
     * @param page The page number to retrieve. Defaults to 1.
     *
     * @return A [Page] object containing a mutable list of [ContactCustomFieldSetting] objects representing the check accounts for the current page.
     */
    suspend fun retrieveContactFieldSettingsWithPagination(
        perPage: Int = 10,
        page: Int = 1
    ): Page<ContactCustomFieldSetting> {
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = retrieveContactFieldSettings(true, limit, offset)

        return ContactCustomFieldSettingPage(
            contactAddressRequests = this,

            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!
        )
    }

    /**
     * Create contact field setting
     *
     * @param name name of the contact fields
     * @param description The description of the contact field
     *
     * @return created [ContactCustomFieldSetting]
     */
    suspend fun createContactFieldSetting(
        name: String,
        description: String? = null
    ): ContactCustomFieldSetting? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.post("ContactCustomFieldSetting") {
                    setJSONObjectBody(buildJsonObject {
                        put("name", name)
                        put("description", description)
                        put("objectName", "ContactCustomFieldSetting")
                    })
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement( ContactCustomFieldSettingSerializer, it["objects"]!!)
            }
        )
    }

    /**
     * Find contact field setting by ID
     *
     * @param contactCustomFieldSettingId ID of contact field to return
     *
     * @return returns a single [ContactCustomFieldSetting]
     */
    suspend fun findContactFieldSettingByID(
        contactCustomFieldSettingId: Int
    ): ContactCustomFieldSetting? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("ContactCustomFieldSetting/$contactCustomFieldSettingId")
            },
            {
                return@requestWithHandling Util.getObjectFromObjectList(it, ContactCustomFieldSettingSerializer)
            }
        )
    }

    /**
     * Update contact field setting
     *
     * Update an existing contact field setting
     *
     * @param contactCustomFieldSettingId ID of contact field setting you want to update
     * @param name name of the contact fields
     * @param description The description of the contact field
     *
     * @return returns a single [ContactCustomFieldSetting]
     */
    suspend fun updateContactFieldSetting(
        contactCustomFieldSettingId: Int,
        name: String? = null,
        description: String? = null
    ): ContactCustomFieldSetting? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.put("ContactCustomFieldSetting/$contactCustomFieldSettingId") {
                    setJSONObjectBody(buildJsonObject {
                        put("name", name)
                        put("description", description)
                        put("objectName", "ContactCustomFieldSetting")
                    })
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(ContactCustomFieldSettingSerializer, it["objects"]!!)
            }
        )
    }

    /**
     * Deletes a contact field setting
     *
     * @param contactCustomFieldSettingId Id of contact field to delete
     */
    suspend fun deleteContactFieldSetting(
        contactCustomFieldSettingId: Int
    ) {
        sevDeskAPI.request {
            sevDeskAPI.client.delete("ContactCustomFieldSetting/$contactCustomFieldSettingId")
        }
    }

    /**
     * Receive count reference
     *
     * @param contactCustomFieldSettingId ID of contact field you want to get the reference count
     *
     * @return returns a single [ContactCustomFieldSetting]
     */
    suspend fun receiveCountReference(
        contactCustomFieldSettingId: Int
    ): Int? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("ContactCustomFieldSetting/$contactCustomFieldSettingId/getReferenceCount")
            },
            {
                return@requestWithHandling it["objects"]?.jsonPrimitive?.intOrNull
            }
        )
    }

}