package me.rumbugen.sevdesk.objects.sevDeskFile

import me.rumbugen.sevdesk.objects.SevDeskObject
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Base64
import java.util.Locale

data class SevDeskFile(
    var filename: String?,
    var mimetype: String?,
    var base64Encoded: Boolean?,
    var content: String?,
): SevDeskObject() {
    /**
     * Returns an InputStream from the file content.  If the content is base64 encoded,
     * it is decoded before being returned as a stream.  If the content is null or empty,
     * returns an empty InputStream.
     *
     * @return An InputStream representing the file content.
     */
    fun getStream(): InputStream {
        if (content.isNullOrEmpty()) {
            return ByteArrayInputStream(ByteArray(0))
        }

        return if (base64Encoded == true) {
            val decodedBytes = Base64.getDecoder().decode(content)
            ByteArrayInputStream(decodedBytes)
        } else {
            ByteArrayInputStream(content!!.toByteArray())
        }
    }
}