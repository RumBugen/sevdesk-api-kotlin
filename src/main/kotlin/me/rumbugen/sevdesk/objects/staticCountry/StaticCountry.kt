package me.rumbugen.sevdesk.objects.staticCountry

import me.rumbugen.sevdesk.objects.SevDeskObject
import java.util.Locale

data class StaticCountry(
    override val id: Int,
    var code: String? = null,
    var name: String? = null,
    var nameEn: String? = null,
    var translationCode: String? = null,
): SevDeskObject(id) {
    fun getLocale(): Locale {
        return Locale.Builder().setRegion(code).build()
    }
}