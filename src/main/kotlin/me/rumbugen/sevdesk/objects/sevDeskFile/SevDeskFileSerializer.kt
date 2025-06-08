package me.rumbugen.sevdesk.objects.sevDeskFile

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rumbugen.sevdesk.objects.staticCountry.StaticCountry

@Serializer(forClass = SevDeskFile::class)
object SevDeskFileSerializer : KSerializer<SevDeskFile> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SevDeskFile")

    override fun serialize(encoder: Encoder, value: SevDeskFile) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalStateException("This class can be serialized only by JSON")

        val jsonObject = buildJsonObject {
            put("filename", value.filename)
            put("mimetype", value.mimetype)
            put("base64Encoded", value.base64Encoded)
            put("content", value.content)
        }

        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): SevDeskFile {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalStateException("This class can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return SevDeskFile(
            filename = jsonObject["filename"]?.jsonPrimitive?.content,
            mimetype = jsonObject["mimetype"]?.jsonPrimitive?.content,
            base64Encoded = jsonObject["base64Encoded"]?.jsonPrimitive?.boolean,
            content = jsonObject["content"]?.jsonPrimitive?.content,
        )
    }
}