package me.rumbugen.sevdesk.objects.basic

enum class BookingVersion(val id: String) {
    VERSION_1("1.0"), VERSION_2("2.0");

    companion object {
        fun valueOfVersion(version: String): BookingVersion {
            return entries.toTypedArray().first { it.id == version }
        }
    }
}