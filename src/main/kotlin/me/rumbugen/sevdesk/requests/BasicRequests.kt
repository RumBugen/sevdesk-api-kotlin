package me.rumbugen.sevdesk.requests

import io.ktor.client.request.*
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.rumbugen.sevdesk.SevDeskAPI
import me.rumbugen.sevdesk.Util
import me.rumbugen.sevdesk.objects.basic.BookingVersion
import me.rumbugen.sevdesk.objects.basic.Category
import me.rumbugen.sevdesk.objects.basic.CategorySerializer
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountry
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountrySerializer
import java.util.*

/**
 * Basics
 *
 * This is a collection of basic sevdesk functions that don't really belong into any other category.
 */
class BasicRequests internal constructor(private var sevDeskAPI: SevDeskAPI) {

    /**
     * Retrieve bookkeeping system version
     *
     * To check if you already received the update to version 2.0 you can use this endpoint.
     * @return [BookingVersion]
     */
    suspend fun retrieveBookingVersion(): BookingVersion {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("Tools/bookkeepingSystemVersion")
            },
            {
                return@requestWithHandling BookingVersion.valueOfVersion(it["objects"]!!.jsonObject["version"]!!.jsonPrimitive.content)
            })!!
    }

    /**
     * Retrieves all countries from the sevDesk API.
     *
     * @return A list of `StaticCountry` objects representing all countries,
     *         or an empty mutable list if the request fails or no countries are found.
     */
    suspend fun retrieveCountryList(): List<StaticCountry> {
        return sevDeskAPI.requestWithHandling(
            {
                sevDeskAPI.client.get("StaticCountry") {
                    parameter("countAll", true)
                    parameter("limit", 1000)
                }
            },
            {
                return@requestWithHandling Util.getObjectList(it, StaticCountrySerializer)
            }) ?: mutableListOf()
    }

    /**
     * Retrieves a list of countries, filtered by the provided locale.
     *
     * Many Country codes are wrong, for example
     * Locale.GERMANY is not GERMANY in java the Locale is "de_DE" and in sevDesk only "DE", be careful!
     *
     * @param lang The locale to filter the country list by.
     * @return A `StaticCountry` object that matches the provided locale, or null if no match is found.
     */
    suspend fun retrieveCountryList(lang: Locale): StaticCountry? {
        return retrieveCountryList().first { it.getLocale()?.country == lang.country }
    }

    /**
     * Retrieves a list of categories from the sevDesk API.
     *
     * This function fetches categories and can filter them by a specific type.
     *
     * @param own Parameter that shows all categorys (from sevdesk offical and own categorys created by the sevclient) when its false, true means only own categorys
     * @param type Optional parameter to filter categories by their [Category.Type]. If null, all categories are retrieved regardless of their type.
     * @return A list of [Category] objects. Returns an empty list if the API call is unsuccessful or if no categories matching the provided criteria are found.
     */
    suspend fun retrieveCategories(own: Boolean = false, type: Category.Type? = null): List<Category> {
        return sevDeskAPI.requestWithHandling(
            requestBlock = {
                sevDeskAPI.client.get("Category") {
                    parameter("limit", "1000")
                    if (type != null) parameter("objectType", type.name)
                    parameter("own", own)
                }
            },
            parseBlock = {
                return@requestWithHandling Util.getObjectList(it, CategorySerializer)
            }
        ) ?: emptyList()
    }

}