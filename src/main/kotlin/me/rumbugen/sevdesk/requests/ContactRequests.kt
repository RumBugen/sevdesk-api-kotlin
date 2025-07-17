package me.rumbugen.sevdesk.requests

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.Util
import me.rumbugen.sevdesk.Util.setJSONObjectBody
import me.rumbugen.sevdesk.Util.toInt
import me.rumbugen.sevdesk.objects.Sorting
import me.rumbugen.sevdesk.objects.contact.Contact
import me.rumbugen.sevdesk.objects.contact.ContactFindByCustomFieldPage
import me.rumbugen.sevdesk.objects.contact.ContactNumberItems
import me.rumbugen.sevdesk.objects.contact.ContactPage
import me.rumbugen.sevdesk.objects.contact.ContactSerializer
import java.time.LocalDate
import java.time.OffsetDateTime
import kotlin.Int

/**
 * ContactRequests
 *
 * Provides functionality to interact with contacts in sevDesk.
 *
 * Every time a sevDesk customer sells/buys an item or provides a service, there is an end customer involved.
 * These end-customers need to be tracked in sevDesk as customers link them to invoices, orders, receipts, and other bookkeeping documents.
 *
 * Types of contacts:
 *
 * Contacts can be either a person or an organization.
 * Currently, both share the same set of attributes, but some values might not make sense for both types.
 *
 * How to determine contact type:
 *
 * Contacts are differentiated by the attributes `name`, `surname`, and `familyname`.
 * If `surname` OR `familyname` has a value, the contact is considered an individual person.
 * If `name` has a value, it is considered an organization.
 *
 * Contact Categories:
 *
 * Every contact belongs to one of the following categories:
 *
 *   - Supplier (ID: 2)
 *   - Customer (ID: 3)
 *   - Partner (ID: 4)
 *   - Prospect Customer (ID: 28)
 */
class ContactRequests internal constructor(private var sevDeskAPI: SevDeskAPI) {

    /**
     * Get next free customer number
     *
     * Retrieves the next available customer number.
     *
     * @return The next free customer number as an [Int].
     */
    suspend fun getNextFreeCustomerNumber(): Int {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Contact/Factory/getNextCustomerNumber")
            },
            {
                it["objects"]?.jsonPrimitive?.intOrNull
            }
        ) ?: 0
    }

    /**
     * Find contacts by custom field value.
     *
     * Returns a list of contacts that have a specific custom field set to a given value.
     * One of customFieldId or customFieldName is required.
     *
     * @param value The value to be checked.
     * @param customFieldId ID of ContactCustomFieldSetting for which the value has to be checked.
     * @param customFieldName The ContactCustomFieldSetting name, if no ContactCustomFieldSetting is provided.
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return A [Pair] containing:
     *  - A [List] of [Contact] objects that match the specified custom field value.
     *  - An [Int] representing the total number of contacts matching the criteria, if `countAll` is `true`; otherwise, `null`.
     */
    suspend fun findContactsByCustomFieldValue(
        value: String,
        customFieldId: Int?,
        customFieldName: String?,
        // pagination
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null
    ): Pair<List<Contact>, Int?> {
        require(customFieldId == null && customFieldName == null) { "customFieldId or customFieldName must be not null!" }

        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Contact/Factory/findContactsByCustomFieldValue") {
                    parameter("value", value)
                    customFieldId?.let {
                        parameter("customFieldSetting[id]", it)
                        parameter("customFieldSetting[objectName]", "ContactCustomFieldSetting")
                    }
                    customFieldName?.let { parameter("customFieldName", customFieldName) }
                    parameter("countAll", countAll)
                    limit?.let { parameter("limit", it) }
                    offset?.let { parameter("offset", offset) }
                }
            },
            {
                Util.getObjectList(it, ContactSerializer).to(it["total"]?.jsonPrimitive?.contentOrNull?.toInt())
            }
        ) ?: emptyList<Contact>().to(null)
    }


    /**
     * Find contacts by custom field value.
     *
     * Returns a list of contacts that have a specific custom field set to a given value.
     * One of customFieldId or customFieldName is required.
     *
     * @param value The value to be checked.
     * @param customFieldId ID of ContactCustomFieldSetting for which the value has to be checked.
     * @param customFieldName The ContactCustomFieldSetting name, if no ContactCustomFieldSetting is provided.
     *
     * @param limit An optional integer specifying the maximum number of contacts to retrieve. If `null`, all matching contacts are returned.
     * @param offset An optional integer specifying the index of the first contact to retrieve. If `null`, retrieval starts from the beginning (index 0).
     *
     * @return A [List] of [Contact] objects that match the specified custom field value.
     */
    suspend fun findContactsByCustomFieldValueWithoutPagination(
        value: String,
        customFieldId: Int?,
        customFieldName: String?,
        // pagination
        limit: Int? = null,
        offset: Int? = null
    ): List<Contact> {
        return findContactsByCustomFieldValue(value, customFieldId, customFieldName, false, limit, offset).first
    }

    /**
     * Find contacts by custom field value.
     *
     * Returns a list of contacts that have a specific custom field set to a given value.
     * One of customFieldId or customFieldName is required.
     *
     * @param value The value to be checked.
     * @param customFieldId ID of ContactCustomFieldSetting for which the value has to be checked.
     * @param customFieldName The ContactCustomFieldSetting name, if no ContactCustomFieldSetting is provided.
     *
     * @param perPage The number of objects to retrieve per page. Defaults to 10.
     * @param page The page number to retrieve. Defaults to 1.
     *
     * @return A [Page] of [Contact]
     */
    suspend fun findContactsByCustomFieldValueWithPagination(
        value: String,
        customFieldId: Int?,
        customFieldName: String?,
        // pagination
        perPage: Int = 10,
        page: Int = 1
    ): Page<Contact> {
        require(customFieldId == null && customFieldName == null) { "customFieldId or customFieldName must be not null!" }
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = findContactsByCustomFieldValue(value, customFieldId, customFieldName, true, limit, offset)

        return ContactFindByCustomFieldPage(
            contactRequests = this,

            value = value,
            customFieldId = customFieldId,
            customFieldName = customFieldName,

            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!
        )
    }

    /**
     * Check if a customer number is available
     *
     * Checks if a given customer number is available or already used.
     *
     * @param customerNumber The customer number to be checked.
     *
     * @return A [Boolean] is returned. True if the number is free
     */
    suspend fun customerNumberAvailable(customerNumber: String): Boolean {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Contact/Mapper/checkCustomerNumberAvailability") {
                    parameter("customerNumber", customerNumber)
                }
            },
            {
                it["objects"]?.jsonPrimitive?.booleanOrNull
            }
        ) ?: false
    }

    /**
     * Retrieve contacts
     *
     * There are a multitude of parameter which can be used to filter.
     *
     * @param depth Retrieve organisations AND persons.
     * This attribute is not active as default, so if you don't specify it as true, you will only get organisations.
     * @param categoryId Only retrieve all contacts with id as a category
     * @param city Only retrieve all contacts with cityName as a city
     * @param tags Only retrieve all contacts with id as a tag. You can do it with multiple tags.
     * @param customerNumber Only retrieve all contacts with number as a customer number
     * @param parentId Only retrieve all contacts with id as a parent organisation
     * @param name Only retrieve all contacts with name as a name, surename or familyname
     * @param zip Only retrieve all contacts with zipCode as a zip
     * @param countryId Only retrieve all contacts with id as a country
     * @param createBefore Only retrieve all contacts created before timestamp
     * @param createAfter Only retrieve all contacts created after timestamp
     * @param updateBefore Only retrieve all contacts updated last before timestamp
     * @param updateAfter Only retrieve all contacts updated last after timestamp
     * @param orderByCustomerNumber Order all contacts after customer number in ASC/DESC order TODO: Enum???
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return A [Pair] containing:
     *  - A [List] of [Contact] objects that match the specified custom field value.
     *  - An [Int] representing the total number of contacts matching the criteria, if `countAll` is `true`; otherwise, `null`.
     */
    suspend fun retrieveContacts(
        depth: Boolean = false,
        categoryId: Int? = null,
        city: String? = null,
        tags: List<Int>? = null,
        customerNumber: Int? = null,
        parentId: Int? = null,
        name: String? = null,
        zip: Int? = null,
        countryId: Int? = null,
        createBefore: OffsetDateTime? = null,
        createAfter: OffsetDateTime? = null,
        updateBefore: OffsetDateTime? = null,
        updateAfter: OffsetDateTime? = null,
        orderByCustomerNumber: Sorting? = null,
        // pagination
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null
    ): Pair<List<Contact>, Int?> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Contact") {
                    parameter("depth", depth.toInt())
                    customerNumber?.let { parameter("customerNumber", it) }
                    categoryId?.let {
                        parameter("category[id]", it)
                        parameter("category[objectName]", "Category")
                    }
                    city?.let { parameter("city", it) }
                    tags?.forEachIndexed { id, it ->
                        parameter("tag[$id][id]", it)
                        parameter("tag[$id][objectName]", "Tag")
                    }
                    customerNumber?.let { parameter("customerNumber", it) }
                    parentId?.let {
                        parameter("parent[id]", it)
                        parameter("parent[objectName]", "Contact")
                    }
                    name?.let { parameter("name", it) }
                    zip?.let { parameter("zip", it) }
                    countryId?.let {
                        parameter("country[id]", it)
                        parameter("country[objectName]", "StaticCountry")
                    }
                    createBefore?.let {
                        parameter("createBefore", it.toEpochSecond())
                    }
                    createAfter?.let {
                        parameter("createAfter", it.toEpochSecond())
                    }
                    updateBefore?.let {
                        parameter("updateBefore", it.toEpochSecond())
                    }
                    updateAfter?.let {
                        parameter("updateAfter", it.toEpochSecond())
                    }
                    orderByCustomerNumber?.let {
                        parameter("orderByCustomerNumber", it.name)
                    }
                    parameter("countAll", countAll)
                    limit?.let { parameter("limit", it) }
                    offset?.let { parameter("offset", it) }
                }
            },
            {
                Util.getObjectList(it, ContactSerializer).to(it["total"]?.jsonPrimitive?.contentOrNull?.toInt())
            }
        ) ?: emptyList<Contact>().to(null)
    }

    /**
     * Retrieve contacts
     *
     * There are a multitude of parameter which can be used to filter.
     *
     * @param depth Retrieve organisations AND persons.
     * This attribute is not active as default, so if you don't specify it as true, you will only get organisations.
     * @param categoryId Only retrieve all contacts with id as a category
     * @param city Only retrieve all contacts with cityName as a city
     * @param tags Only retrieve all contacts with id as a tag. You can do it with multiple tags.
     * @param customerNumber Only retrieve all contacts with number as a customer number
     * @param parentId Only retrieve all contacts with id as a parent organisation
     * @param name Only retrieve all contacts with name as a name, surename or familyname
     * @param zip Only retrieve all contacts with zipCode as a zip
     * @param countryId Only retrieve all contacts with id as a country
     * @param createBefore Only retrieve all contacts created before timestamp
     * @param createAfter Only retrieve all contacts created after timestamp
     * @param updateBefore Only retrieve all contacts updated last before timestamp
     * @param updateAfter Only retrieve all contacts updated last after timestamp
     * @param orderByCustomerNumber Order all contacts after customer number in ASC/DESC order TODO: Enum???
     *
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return A [List] of [Contact] objects that match the specified custom field value.
     */
    suspend fun retrieveContactsWithoutPagination(
        depth: Boolean = false,
        categoryId: Int? = null,
        city: String? = null,
        tags: List<Int>? = null,
        customerNumber: Int? = null,
        parentId: Int? = null,
        name: String? = null,
        zip: Int? = null,
        countryId: Int? = null,
        createBefore: OffsetDateTime? = null,
        createAfter: OffsetDateTime? = null,
        updateBefore: OffsetDateTime? = null,
        updateAfter: OffsetDateTime? = null,
        orderByCustomerNumber: Sorting? = null,
        // pagination
        limit: Int? = null,
        offset: Int? = null
    ): List<Contact> {
        return retrieveContacts(
            depth,
            categoryId,
            city,
            tags,
            customerNumber,
            parentId,
            name,
            zip,
            countryId,
            createBefore,
            createAfter,
            updateBefore,
            updateAfter,
            orderByCustomerNumber,
            true,
            limit,
            offset
        ).first
    }

    /**
     * Retrieve contacts
     *
     * There are a multitude of parameter which can be used to filter.
     *
     * @param depth Retrieve organisations AND persons.
     * This attribute is not active as default, so if you don't specify it as true, you will only get organisations.
     * @param categoryId Only retrieve all contacts with id as a category
     * @param city Only retrieve all contacts with cityName as a city
     * @param tags Only retrieve all contacts with id as a tag. You can do it with multiple tags.
     * @param customerNumber Only retrieve all contacts with number as a customer number
     * @param parentId Only retrieve all contacts with id as a parent organisation
     * @param name Only retrieve all contacts with name as a name, surename or familyname
     * @param zip Only retrieve all contacts with zipCode as a zip
     * @param countryId Only retrieve all contacts with id as a country
     * @param createBefore Only retrieve all contacts created before timestamp
     * @param createAfter Only retrieve all contacts created after timestamp
     * @param updateBefore Only retrieve all contacts updated last before timestamp
     * @param updateAfter Only retrieve all contacts updated last after timestamp
     * @param orderByCustomerNumber Order all contacts after customer number in ASC/DESC order
     *
     * @param perPage The number of objects to retrieve per page. Defaults to 10.
     * @param page The page number to retrieve. Defaults to 1.
     *
     * @return A [Page] of [Contact]
     */
    suspend fun retrieveContactsWithPagination(
        depth: Boolean = false,
        categoryId: Int? = null,
        city: String? = null,
        tags: List<Int>? = null,
        customerNumber: Int? = null,
        parentId: Int? = null,
        name: String? = null,
        zip: Int? = null,
        countryId: Int? = null,
        createBefore: OffsetDateTime? = null,
        createAfter: OffsetDateTime? = null,
        updateBefore: OffsetDateTime? = null,
        updateAfter: OffsetDateTime? = null,
        orderByCustomerNumber: Sorting? = null,
        // pagination
        perPage: Int = 10,
        page: Int = 1
    ): Page<Contact> {
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = retrieveContacts(
            depth,
            categoryId,
            city,
            tags,
            customerNumber,
            parentId,
            name,
            zip,
            countryId,
            createBefore,
            createAfter,
            updateBefore,
            updateAfter,
            orderByCustomerNumber,
            true,
            perPage,
            page
        )

        return ContactPage(
            contactRequests = this,

            depth,
            categoryId,
            city,
            tags,
            customerNumber,
            parentId,
            name,
            zip,
            countryId,
            createBefore,
            createAfter,
            updateBefore,
            updateAfter,
            orderByCustomerNumber,

            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!
        )
    }

    /**
     * Create a new contact
     *
     * Creates a new contact.
     * For adding addresses and communication ways, you will need to use the ContactAddress and CommunicationWay endpoints.
     *
     * @param name The organization name.
     * Be aware that the type of contact will depend on this attribute.
     * If it holds a value, the contact will be regarded as an organization.
     * @param categoryId Category of the contact.
     * @param status Defines the status of the contact.
     * @param customerNumber The customer number
     * @param parentId The parent contact to which this contact belongs. Must be an organization.
     * @param surename The first name of the contact.
     * Yeah... not quite right in literally every way. We know.
     * Not to be used for organizations.
     * @param familyname The last name of the contact.
     * Not to be used for organizations.
     * @param titel A non-academic title for the contact.
     * Not to be used for organizations.
     * @param description A description for the contact.
     * @param academicTitle A academic title for the contact.
     * Not to be used for organizations.
     * @param gender Gender of the contact.
     * Not to be used for organizations.
     * @param name2 Second name of the contact.
     * Not to be used for organizations.
     * @param birthday Birthday of the contact.
     * Not to be used for organizations.
     * @param vatNumber Vat number of the contact.
     * @param bankAccount Bank account number (IBAN) of the contact.
     * @param bankNumber Bank number of the bank used by the contact.
     * @param defaultCashbackTime Absolute time in days which the contact has to pay his invoices and subsequently get a cashback.
     * @param defaultCashbackPercent Percentage of the invoice sum the contact gets back if he paid invoices in time.
     * @param defaultTimeToPay The payment goal in days which is set for every invoice of the contact.
     * @param taxNumber The tax number of the contact.
     * @param taxOffice The tax office of the contact (only for greek customers).
     * @param exemptVat Defines if the contact is freed from paying vat.
     * @param defaultDiscountAmount The default discount the contact gets for every invoice.
     * Depending on defaultDiscountPercentage attribute, in percent or absolute value.
     * @param defaultDiscountPercentage Defines if the discount is a percentage (true) or an absolute value (false).
     * @param buyerReference Buyer reference of the contact.
     * @param governmentAgency Defines whether the contact is a government agency (true) or not (false).
     *
     * @return The created [Contact] object, or null if the creation failed.
     */
    suspend fun createContact(
        name: String? = null,
        categoryId: Int,
        status: Contact.Status? = null,
        parentId: Int? = null,
        customerNumber: String? = null,
        surename: String? = null,
        familyname: String? = null,
        titel: String? = null,
        description: String? = null,
        academicTitle: String? = null,
        gender: String? = null,
        name2: String? = null,
        birthday: LocalDate? = null,
        vatNumber: String? = null,
        bankAccount: String? = null,
        bankNumber: String? = null,
        defaultCashbackTime: Int? = null,
        defaultCashbackPercent: Float? = null,
        defaultTimeToPay: Int? = null,
        taxNumber: String? = null,
        taxOffice: String? = null,
        exemptVat: Boolean? = null,
        defaultDiscountAmount: Float? = null,
        defaultDiscountPercentage: Boolean? = null,
        buyerReference: String? = null,
        governmentAgency: Boolean? = null,
    ): Contact? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.post("Contact") {
                    val jsonBody: JsonObject = buildJsonObject {
                        put("name", name)
                        put("category", buildJsonObject {
                            put("id", categoryId)
                            put("objectName", "Category")
                        })
                        put("status", status?.idRepresentation)
                        parentId?.let {
                            put("parent", buildJsonObject {
                                put("id", parentId)
                                put("objectId", "Contact")
                            })
                        }
                        put("customerNumber", customerNumber)
                        put("surename", surename)
                        put("familyname", familyname)
                        put("titel", titel)
                        put("description", description)
                        put("academicTitle", academicTitle)
                        put("gender", gender)
                        put("name2", name2)
                        put("birthday", birthday.toString())
                        put("vatNumber", vatNumber)
                        put("bankAccount", bankAccount)
                        put("bankNumber", bankNumber)
                        put("defaultCashbackTime", defaultCashbackTime)
                        put("defaultCashbackPercent", defaultCashbackPercent)
                        put("defaultTimeToPay", defaultTimeToPay)
                        put("taxNumber", taxNumber)
                        put("taxOffice", taxOffice)
                        put("exemptVat", exemptVat)
                        put("defaultDiscountAmount", defaultDiscountAmount)
                        put("defaultDiscountPercentage", defaultDiscountPercentage)
                        put("buyerReference", buyerReference)
                        put("governmentAgency", governmentAgency)
                    }

                    setJSONObjectBody(jsonBody)
                }
            },
            {
                Json.decodeFromJsonElement(ContactSerializer, it["objects"]!!)
            }
        )
    }


    /**
     * Find contact by ID
     *
     * Returns a single contact
     *
     * @param contactId ID of contact to return
     *
     * @return The [Contact] object if found, otherwise null.
     */
    suspend fun findContactByID(
        contactId: Int,
    ): Contact? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Contact/${contactId}")
            },
            {
                Util.getObjectList(it, ContactSerializer).getOrNull(0)
            }
        )
    }

    /**
     * Update a existing contact
     *
     * Update a contact
     *
     * @param contactId ID of contact to update
     * @param name The organization name.
     * Be aware that the type of contact will depend on this attribute.
     * If it holds a value, the contact will be regarded as an organization.
     * @param categoryId Category of the contact.
     * @param status Defines the status of the contact.
     * @param customerNumber The customer number
     * @param parentId The parent contact to which this contact belongs. Must be an organization.
     * @param surename The first name of the contact.
     * Yeah... not quite right in literally every way. We know.
     * Not to be used for organizations.
     * @param familyname The last name of the contact.
     * Not to be used for organizations.
     * @param titel A non-academic title for the contact.
     * Not to be used for organizations.
     * @param description A description for the contact.
     * @param academicTitle A academic title for the contact.
     * Not to be used for organizations.
     * @param gender Gender of the contact.
     * Not to be used for organizations.
     * @param name2 Second name of the contact.
     * Not to be used for organizations.
     * @param birthday Birthday of the contact.
     * Not to be used for organizations.
     * @param vatNumber Vat number of the contact.
     * @param bankAccount Bank account number (IBAN) of the contact.
     * @param bankNumber Bank number of the bank used by the contact.
     * @param defaultCashbackTime Absolute time in days which the contact has to pay his invoices and subsequently get a cashback.
     * @param defaultCashbackPercent Percentage of the invoice sum the contact gets back if he paid invoices in time.
     * @param defaultTimeToPay The payment goal in days which is set for every invoice of the contact.
     * @param taxNumber The tax number of the contact.
     * @param taxOffice The tax office of the contact (only for greek customers).
     * @param exemptVat Defines if the contact is freed from paying vat.
     * @param defaultDiscountAmount The default discount the contact gets for every invoice.
     * Depending on defaultDiscountPercentage attribute, in percent or absolute value.
     * @param defaultDiscountPercentage Defines if the discount is a percentage (true) or an absolute value (false).
     * @param buyerReference Buyer reference of the contact.
     * @param governmentAgency Defines whether the contact is a government agency (true) or not (false).
     *
     * @return The updated [Contact] object, or null if it fails.
     */
    suspend fun updateContact(
        contactId: Int,
        name: String? = null,
        categoryId: Int? = null,
        status: Contact.Status? = null,
        parentId: Int? = null,
        customerNumber: String? = null,
        surename: String? = null,
        familyname: String? = null,
        titel: String? = null,
        description: String? = null,
        academicTitle: String? = null,
        gender: String? = null,
        name2: String? = null,
        birthday: LocalDate? = null,
        vatNumber: String? = null,
        bankAccount: String? = null,
        bankNumber: String? = null,
        defaultCashbackTime: Int? = null,
        defaultCashbackPercent: Float? = null,
        defaultTimeToPay: Int? = null,
        taxNumber: String? = null,
        taxOffice: String? = null,
        exemptVat: Boolean? = null,
        defaultDiscountAmount: Float? = null,
        defaultDiscountPercentage: Boolean? = null,
        buyerReference: String? = null,
        governmentAgency: Boolean? = null,
    ): Contact? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.put("Contact/${contactId}") {
                    val jsonObject: JsonObject = buildJsonObject {
                        put("name", name)
                        put("status", status?.idRepresentation)
                        put("customerNumber", customerNumber)
                        put("parentId", parentId)
                        put("customerNumber", customerNumber)
                        put("surename", surename)
                        put("familyname", familyname)
                        put("titel", titel)
                        put("description", description)
                        put("academicTitle", academicTitle)
                        put("gender", gender)
                        put("name2", name2)
                        put("birthday", birthday.toString())
                        put("vatNumber", vatNumber)
                        put("bankAccount", bankAccount)
                        put("bankNumber", bankNumber)
                        put("defaultCashbackTime", defaultCashbackTime)
                        put("defaultCashbackPercent", defaultCashbackPercent)
                        put("defaultTimeToPay", defaultTimeToPay)
                        put("taxNumber", taxNumber)
                        put("taxOffice", taxOffice)
                        put("exemptVat", exemptVat)
                        put("defaultDiscountAmount", defaultDiscountAmount)
                        put("defaultDiscountPercentage", defaultDiscountPercentage)
                        put("buyerReference", buyerReference)
                        put("governmentAgency", governmentAgency)
                    }
                    setJSONObjectBody(jsonObject)
                }
            },
            {
                Json.decodeFromJsonElement(ContactSerializer, it["objects"]!!)
            }
        )
    }

    /**
     * Updates an existing contact.
     *
     * Only specific values of the provided [contact] are used for the update.
     *
     * @param contact The contact object containing the updated values.  The `id` field of this contact is used to identify the contact to update.
     * @return The updated [Contact] object, or `null` if the update fails.
     */
    suspend fun updateContact(
        contact: Contact
    ): Contact? {
        return updateContact(
            contact.id,
            contact.name,
            contact.category?.id,
            contact.status,
            contact.parent?.id,
            contact.customerNumber,
            contact.surename,
            contact.familyname,
            contact.titel,
            contact.description,
            contact.academicTitle,
            contact.gender,
            contact.name2,
            contact.birthday,
            contact.vatNumber,
            contact.bankAccount,
            contact.bankNumber,
            contact.defaultCashbackTime,
            contact.defaultDiscountAmount,
            contact.defaultTimeToPay,
            contact.taxNumber,
            contact.taxOffice,
            contact.exemptVat,
            contact.defaultDiscountAmount,
            contact.defaultDiscountPercentage,
            contact.buyerReference,
            contact.governmentAgency
        )
    }

    /**
     * Delete a contact.
     *
     * @param contactId ID of contact to delete.
     */
    suspend fun deleteContact(
        contactId: Int
    ) {
        sevDeskAPI.request {
            sevDeskAPI.client.delete("Contact/$contactId")
        }
    }

    /**
     * Get the number of all items associated with a specific contact.
     *
     * This function retrieves a map containing the count of various item types
     * (invoices, orders, etc.) linked to the provided contact ID.
     *
     * @param contactId The unique identifier of the contact.
     * @return A map where the keys are `ContactNumberItems` representing different item types,
     *         and the values are the corresponding counts for that contact.
     */
    suspend fun getNumberOfAllItems(
        contactId: Int
    ): Map<ContactNumberItems, Int> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Contact/$contactId/getTabsItemCount")
            },
            { it ->
                val map = mutableMapOf<ContactNumberItems, Int>()
                it["objects"]?.jsonObject?.forEach {
                    val item: ContactNumberItems = ContactNumberItems.entries.first { item ->
                        if (item.overrideValue != null) item.overrideValue == it.key else item.name.lowercase() == it.key
                    }

                    map.put(item, it.value.jsonPrimitive.int)
                }

                map
            }
        ) ?: emptyMap()
    }
}