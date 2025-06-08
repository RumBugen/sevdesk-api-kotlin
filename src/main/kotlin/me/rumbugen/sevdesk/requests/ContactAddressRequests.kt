package me.rumbugen.sevdesk.requests

import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.Util
import me.rumbugen.sevdesk.Util.putSerializedIfExists
import me.rumbugen.sevdesk.Util.setJSONObjectBody
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.basic.Category
import me.rumbugen.sevdesk.objects.basic.CategorySerializer
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.contact.ContactSerializer
import me.rumbugen.sevdesk.objects.contact.address.ContactAddress
import me.rumbugen.sevdesk.objects.contact.address.ContactAddressPage
import me.rumbugen.sevdesk.objects.contact.address.ContactAddressSerializer
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountry
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountrySerializer

/**
 * ContactAddress
 *
 * As one contact can have multiple addresses, they can not be part of the contact attributes.
 * Instead, they have their own endpoint which makes it possible to create as many addresses for one contact as needed.
 * For creating contact addresses have a look at our Swagger specification. Everything should be pretty straight forward.
 */
class ContactAddressRequests internal constructor(private var sevDeskAPI: SevDeskAPI) {

    /**
     * Create a new contact address
     *
     * Creates a new contact address.
     *
     * @param contact The contact to which this contact address belongs.
     * @param street Street name
     * @param zip Zip code
     * @param city City name
     * @param country Country of the contact address.
     * @param category Category of the contact address.
     * @param name Name in address
     * @param name2 Second name in address
     * @param name3 Third name in address
     * @param name4 Fourth name in address
     *
     * @return [ContactAddress] object if found
     */
    suspend fun createNewContactAddress(
        contact: Contact,
        street: String? = null,
        zip: String? = null,
        city: String? = null,
        country: StaticCountry,
        category: Category,
        name: String? = null,
        name2: String? = null,
        name3: String? = null,
        name4: String? = null,
    ): ContactAddress? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.post("ContactAddress") {
                    setJSONObjectBody(buildJsonObject {
                        putSerializedIfExists("contact", ContactSerializer, contact)
                        put("street", street)
                        put("zip", zip)
                        put("city", city)
                        putSerializedIfExists("country", StaticCountrySerializer, country)
                        putSerializedIfExists("category", CategorySerializer, category)
                        put("name", name)
                        put("name2", name2)
                        put("name3", name3)
                        put("name4", name4)
                    })
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(ContactAddressSerializer, it["objects"]!!)
            }
        )
    }

    /**
     * Retrieve contact addresses
     *
     * Retrieve all contact addresses
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return [Pair] A Pair containing:
     *   - A List of [ContactAddress] objects.
     *   - An optional Integer representing the total number of [ContactAddress]'s in sevDesk if `countAll` is true, otherwise null.
     */
    suspend fun retrieveContactAddresses(
        countAll: Boolean = false,
        limit: Int?,
        offset: Int?
    ): Pair<List<ContactAddress>, Int?> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("ContactAddress") {
                    parameter("countAll", countAll)
                    if (limit != null) parameter("limit", limit)
                    if (offset != null) parameter("offset", offset)
                }
            },
            {
                Util.getObjectList(it, ContactAddressSerializer).to(it["total"]?.jsonPrimitive?.contentOrNull?.toInt())
            }) ?: mutableListOf<ContactAddress>().to(null)
    }

    /**
     * Retrieve contact addresses
     *
     * Retrieve all contact addresses
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return A [List] of [ContactAddress] objects.
     */
    suspend fun retrieveContactAddressesWithoutPagination(
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null
    ): List<ContactAddress> {
        return retrieveContactAddresses(countAll, limit, offset).first
    }

    /**
     * Retrieve contact addresses
     *
     * Retrieve all contact addresses
     *
     * This method fetches objects, applying pagination based on the provided `perPage` and `page` values.
     * It internally uses the "withoutPagination" method to fetch the data with specified limit and offset.
     *
     * @param perPage The number of objects to retrieve per page. Defaults to 10.
     * @param page The page number to retrieve. Defaults to 1.
     *
     * @return A [Page] object containing a mutable list of [ContactAddress] objects representing the check accounts for the current page.
     */
    suspend fun retrieveContactAddressesWithPagination(
        perPage: Int = 10,
        page: Int = 1
    ): Page<ContactAddress> {
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = retrieveContactAddresses(true, limit, offset)

        return ContactAddressPage(
            contactAddressRequests = this,

            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!
        )
    }

    /**
     * Find contact address by ID
     *
     * Returns a single contact address
     *
     * @param contactAddressId ID of contact address to return
     *
     * @return [ContactAddress] object if it exists
     */
    suspend fun findContactAddressByID(
        contactAddressId: Int
    ): ContactAddress? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("ContactAddress/$contactAddressId")
            },
            {
                return@requestWithHandling Util.getObjectFromObjectList(it, ContactAddressSerializer)
            }
        )
    }

    /**
     * Update a existing contact address
     *
     * @param contactAddressId ID of contact address to return
     * @param contactId The contact to which this contact address belongs.
     * @param street Street name
     * @param zip Zip code
     * @param city City name
     * @param country Country of the contact address.
     * @param category Category of the contact address.
     * @param name Name in address
     * @param name2 Second name in address
     * @param name3 Third name in address
     * @param name4 Fourth name in address
     *
     * @return Updated [ContactAddress] or null if failed
     */
    suspend fun updateExistingContactAddress(
        contactAddressId: Int,
        contactId: Contact? = null,
        street: String? = null,
        zip: String? = null,
        city: String? = null,
        country: StaticCountry? = null,
        category: Category? = null,
        name: String? = null,
        name2: String? = null,
        name3: String? = null,
        name4: String? = null,
    ): ContactAddress? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.post("ContactAddress/$contactAddressId") {
                    setJSONObjectBody(buildJsonObject {
                        putSerializedIfExists("contact", ContactSerializer, contactId)
                        put("street", street)
                        put("zip", zip)
                        put("city", city)
                        putSerializedIfExists("country", StaticCountrySerializer, country)
                        putSerializedIfExists("category", CategorySerializer, category)
                        put("name", name)
                        put("name2", name2)
                        put("name3", name3)
                        put("name4", name4)
                    })
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement( ContactAddressSerializer, it["objects"]!!)
            }
        )
    }

    /**
     * Update a existing contact address
     *
     * @param contactAddress Updates the [ContactAddress]
     *
     * @return Updated [ContactAddress] or null if failed
     */
    suspend fun updateExistingContactAddress(
        contactAddress: ContactAddress
    ): ContactAddress? {
        return updateExistingContactAddress(
            contactAddress.id,
            contactAddress.contact,
            contactAddress.street,
            contactAddress.zip,
            contactAddress.city,
            contactAddress.country,
            contactAddress.category,
            contactAddress.name,
        )
    }

    /**
     * Deletes a contact address
     *
     * @param contactAddressId Id of contact address resource to delete
     */
    suspend fun deleteContactAddress(
        contactAddressId: Int
    ) {
        sevDeskAPI.request {
            sevDeskAPI.client.delete("ContactAddress/$contactAddressId")
        }
    }

}