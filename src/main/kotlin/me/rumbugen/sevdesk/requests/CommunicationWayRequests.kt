package me.rumbugen.sevdesk.requests

import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.Util
import me.rumbugen.sevdesk.Util.putSerializedIfExists
import me.rumbugen.sevdesk.Util.setJSONObjectBody
import me.rumbugen.sevdesk.Util.toInt
import me.rumbugen.sevdesk.objects.Page
import me.rumbugen.sevdesk.objects.communicationWay.CommunicationWay
import me.rumbugen.sevdesk.objects.communicationWay.CommunicationWay.Type
import me.rumbugen.sevdesk.objects.communicationWay.CommunicationWayPage
import me.rumbugen.sevdesk.objects.communicationWay.CommunicationWaySerializer
import me.rumbugen.sevdesk.objects.communicationWay.key.CommunicationWayKey
import me.rumbugen.sevdesk.objects.communicationWay.key.CommunicationWayKeySerializer

/**
 * A communication way is a medium with which a contact can be reached out to.
 *
 * They cannot be a part of the contact attributes for the same reason as addresses.
 * Each communication way also is of one of the following types, called communication way keys.
 */
class CommunicationWayRequests internal constructor(private var sevDeskAPI: SevDeskAPI) {

    /**
     * Retrieve communication way keys
     *
     * Returns all communication way keys.
     *
     * @return A list of [CommunicationWayKey]
     */
    suspend fun retrieveCommunicationWayKeys(): List<CommunicationWayKey> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("CommunicationWayKey")
            },
            {
                return@requestWithHandling Util.getObjectList(it, CommunicationWayKeySerializer)
            }
        ) ?: mutableListOf()
    }

    /**
     * Retrieve communication ways
     *
     * Returns all communication ways which have been added up until now. Filters can be added.
     *
     * @param contactId ID of contact for which you want the communication ways.
     * @param type Type of the communication ways you want to get.
     * @param main Define if you only want the main communication way.
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return [Pair] A Pair containing:
     *   - A List of [CommunicationWay] objects.
     *   - An optional Integer representing the total number of [CommunicationWay]'s in sevDesk if `countAll` is true, otherwise null.
     */
    suspend fun retrieveCommunicationWays(
        contactId: Int? = null,
        type: Type? = null,
        main: Boolean? = null,
        // pagination
        countAll: Boolean = false,
        limit: Int?,
        offset: Int?
    ): Pair<List<CommunicationWay>, Int?> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("CommunicationWay") {
                    contactId?.let {
                        parameter("contact[id]", contactId)
                        parameter("contact[objectName]", "Contact")
                    }
                    type?.let {
                        parameter("type", type.name)
                    }
                    main?.let {
                        parameter("main", main.toInt().toString())
                    }

                    // Paging
                    parameter("countAll", countAll)
                    if (limit != null) parameter("limit", limit)
                    if (offset != null) parameter("offset", offset)
                }
            },
            {
                Util.getObjectList(it, CommunicationWaySerializer)
                    .to(it["total"]?.jsonPrimitive?.contentOrNull?.toInt())
            }) ?: mutableListOf<CommunicationWay>().to(null)
    }

    /**
     * Retrieve communication ways
     *
     * Returns all communication ways which have been added up until now. Filters can be added.
     *
     * @param contactId ID of contact for which you want the communication ways.
     * @param type Type of the communication ways you want to get.
     * @param main Define if you only want the main communication way.
     *
     * @param countAll A boolean indicating whether to retrieve the total count of objects.
     * @param limit An optional integer specifying the maximum number of objects to retrieve. If null, no limit is applied.
     * @param offset An optional integer specifying the starting index for retrieving objects. If null, retrieval starts from the beginning.
     *
     * @return A [List] of [CommunicationWay] objects.
     */
    suspend fun retrieveCommunicationWaysWithoutPagination(
        contactId: Int? = null,
        type: Type? = null,
        main: Boolean? = null,
        // pageination
        countAll: Boolean = false,
        limit: Int? = null,
        offset: Int? = null
    ): List<CommunicationWay> {
        return retrieveCommunicationWays(contactId, type, main, countAll, limit, offset).first
    }

    /**
     * Retrieve communication ways
     *
     * Returns all communication ways which have been added up until now. Filters can be added.
     *
     * This method fetches objects, applying pagination based on the provided `perPage` and `page` values.
     * It internally uses the "withoutPagination" method to fetch the data with specified limit and offset.
     *
     * @see retrieveCommunicationWaysWithoutPagination Is used internally to manual pagination
     *
     * @param contactId ID of contact for which you want the communication ways.
     * @param type Type of the communication ways you want to get.
     * @param main Define if you only want the main communication way.
     *
     * @param perPage The number of objects to retrieve per page. Defaults to 10.
     * @param page The page number to retrieve. Defaults to 1.
     *
     * @return A [Page] object containing a mutable list of [CommunicationWay] objects representing the check accounts for the current page.
     */
    suspend fun retrieveCommunicationWaysWithPagination(
        contactId: Int? = null,
        type: Type? = null,
        main: Boolean? = null,
        // pagination
        perPage: Int = 10,
        page: Int = 1
    ): Page<CommunicationWay> {
        require(page > 0) { "Page is below 0" }
        require(perPage > 0) { "perPage is below 0" }

        val limit = perPage
        val offset = perPage * (page - 1)

        val request = retrieveCommunicationWays(contactId, type, main, true, limit, offset)

        return CommunicationWayPage(
            communicationWayRequests = this,

            contactId,
            type,
            main,

            items = request.first,
            limit = limit,
            offset = offset,
            total = request.second!!
        )
    }

    /**
     * Create a new contact communication way
     *
     * Creates a new contact communication way.
     *
     * @param contactId Unique identifier of the contact
     * @param type Type of the communication way
     * @param value The value of the communication way.
     * For example the phone number, e-mail address or website.
     * @param communicationWayKey The key of the communication way.
     * Similar to the category of addresses.
     * @param main Defines whether the communication way is the main communication way for the contact.
     */
    suspend fun createNewContactCommunicationWay(
        contactId: Int,
        type: Type,
        value: String,
        communicationWayKey: CommunicationWayKey,
        main: Boolean? = null
    ): CommunicationWay? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.post("CommunicationWay") {
                    setJSONObjectBody(buildJsonObject {
                        put("contact", buildJsonObject {
                            put("id", contactId)
                            put("objectName", "Contact")
                        })
                        putSerializedIfExists("key", CommunicationWayKeySerializer, communicationWayKey)
                        put("type", type.name)
                        put("value", value)
                        main?.let { put("main", main) }
                    })
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(CommunicationWaySerializer, it["objects"]!!)
            }
        )
    }

    /**
     * Find communication way by ID
     *
     * Returns a single communication way
     *
     * @param communicationWayId ID of communication way to return
     */
    suspend fun findCommunicationWayByID(
        communicationWayId: Int
    ): CommunicationWay? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("CommunicationWay/$communicationWayId")
            },
            {
                return@requestWithHandling Util.getObjectFromObjectList(it, CommunicationWaySerializer)
            }
        )
    }

    /**
     * Deletes a communication way
     *
     * @param communicationWayId ID of communication way to return
     */
    suspend fun deleteCommunicationWay(
        communicationWayId: Int
    ) {
        sevDeskAPI.request {
            sevDeskAPI.client.delete("CommunicationWay/$communicationWayId")
        }
    }

    /**
     * Update a existing communication way
     *
     * @param communicationWayId ID of CommunicationWay to update
     * @param contactId The contact to which this communication way belongs.
     * @param type Type of the communication way
     * @param value The value of the communication way.
     * For example the phone number, e-mail address or website.
     * @param key The key of the communication way.
     * Similar to the category of addresses.
     * @param main Defines whether the communication way is the main communication way for the contact.
     */
    suspend fun updateExistingCommunicationWay(
        communicationWayId: Int,
        contactId: Int? = null,
        type: Type? = null,
        value: String? = null,
        key: CommunicationWayKey? = null,
        main: Boolean? = null,
    ): CommunicationWay? {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.put("CommunicationWay/$communicationWayId") {
                    setJSONObjectBody(buildJsonObject {
                        contactId?.let {
                            put("contact", buildJsonObject {
                                put("id", it)
                                put("objectName", "Contact")
                            })
                        }
                        type?.let { put("type", type.name) }
                        value?.let { put("value", value) }
                        key?.let { put("key", Json.encodeToJsonElement(CommunicationWayKeySerializer, it)) }
                        main?.let { put("main", main.toInt()) }
                    })
                }
            },
            {
                return@requestWithHandling Json.decodeFromJsonElement(
                    CommunicationWaySerializer,
                    it["objects"]?.jsonObject!!
                )
            }
        )
    }

    /**
     * Update a existing communication way
     *
     * @param communicationWay The [CommunicationWay] object containing the updated information.
     * Only some fields are updated, see the method without [CommunicationWay] to see which fields are updated.
     */
    suspend fun updateExistingCommunicationWay(
        communicationWay: CommunicationWay
    ): CommunicationWay? {
        return updateExistingCommunicationWay(
            communicationWay.id,
            communicationWay.contact?.id,
            communicationWay.type,
            communicationWay.value,
            communicationWay.key,
            communicationWay.main
        )
    }

}