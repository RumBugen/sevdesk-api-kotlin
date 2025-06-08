package me.rumbugen.sevdesk

import com.neutrine.krate.rateLimiter
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.CurlUserAgent
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.http.path
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.rumbugen.sevdesk.objects.contact.ContactNumberItems
import me.rumbugen.sevdesk.requests.BasicRequests
import me.rumbugen.sevdesk.requests.CheckAccountRequests
import me.rumbugen.sevdesk.requests.CheckAccountTransactionRequests
import me.rumbugen.sevdesk.requests.CommunicationWayRequests
import me.rumbugen.sevdesk.requests.ContactAddressRequests
import me.rumbugen.sevdesk.requests.ContactFieldRequests
import me.rumbugen.sevdesk.requests.ContactRequests
import me.rumbugen.sevdesk.requests.ExportRequests
import me.rumbugen.sevdesk.requests.InvoiceRequests

class SevDeskAPI(var apiKey: String) {
    val rateLimiter = rateLimiter(2) // per second (rate-limit on SevDesk is unknown to me)

    internal val client = HttpClient(Apache) {
        defaultRequest {
            this@HttpClient.CurlUserAgent()
            url {
                protocol = URLProtocol.HTTPS
                host = "my.sevdesk.de"
                path("api/v1/")
            }

            header("Authorization", apiKey)
            header("Content-Type", "application/json")
        }
    }


    internal suspend inline fun <T> requestWithHandling(
        crossinline requestBlock: suspend () -> HttpResponse,
        crossinline parseBlock: (JsonObject) -> T?
    ): T? {
        rateLimiter.awaitUntilTake()
        val response = requestBlock()
        val json = Json.decodeFromString<JsonObject>(response.bodyAsText())
        try {
            if (!response.status.isSuccess()) {
                val errorObject = json["error"]
                val errorCode: Int? = errorObject?.jsonObject["code"]?.jsonPrimitive?.contentOrNull?.toInt()

                when (errorCode) {
                    151 -> { // Object already exists
                        return null
                    }
                }

                throw SevDeskErrorException(
                    response.status,
                    errorObject?.jsonObject["message"]?.jsonPrimitive?.contentOrNull,
                    errorCode
                )
            }
        } catch (e: Exception) {
            throw e
        }
        return parseBlock(json)
    }

    internal suspend inline fun request(
        crossinline requestBlock: suspend () -> HttpResponse
    ) {
        rateLimiter.awaitUntilTake()
        val response = requestBlock()
        val json = Json.decodeFromString<JsonObject>(response.bodyAsText())
        try {
            if (!response.status.isSuccess()) {
                val errorObject = json["error"]
                val errorCode: Int? = errorObject?.jsonObject["code"]?.jsonPrimitive?.contentOrNull?.toInt()

                throw SevDeskErrorException(
                    response.status,
                    errorObject?.jsonObject["message"]?.jsonPrimitive?.contentOrNull,
                    errorCode
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun checkAccountRequest(): CheckAccountRequests {
        return CheckAccountRequests(this)
    }

    fun checkAccountTransactionRequest(): CheckAccountTransactionRequests {
        return CheckAccountTransactionRequests(this)
    }

    fun basicsRequest(): BasicRequests {
        return BasicRequests(this)
    }

    fun contactRequest(): ContactRequests {
        return ContactRequests(this)
    }

    fun contactAddressRequest(): ContactAddressRequests {
        return ContactAddressRequests(this)
    }

    fun contactFieldRequest(): ContactFieldRequests {
        return ContactFieldRequests(this)
    }

    fun communicationWayRequest(): CommunicationWayRequests {
        return CommunicationWayRequests(this)
    }

    fun exportRequest(): ExportRequests {
        return ExportRequests(this)
    }

    fun invoiceRequest(): InvoiceRequests {
        return InvoiceRequests(this)
    }
}