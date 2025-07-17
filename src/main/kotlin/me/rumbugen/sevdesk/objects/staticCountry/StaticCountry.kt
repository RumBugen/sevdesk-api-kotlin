package me.rumbugen.sevdesk.objects.staticCountry

import me.rumbugen.sevdesk.objects.SevDeskObject
import java.util.IllformedLocaleException
import java.util.Locale

data class StaticCountry(
    override val id: Int,
    var code: String? = null,
    var name: String? = null,
    var nameEn: String? = null,
    var translationCode: String? = null,
): SevDeskObject(id) {
    fun getLocale(): Locale? {
        return try {
            Locale.Builder().setRegion(code!!.uppercase()).build()
        }catch (_: IllformedLocaleException) { // They have one Country named "unknown"...
            null
        }
    }
}