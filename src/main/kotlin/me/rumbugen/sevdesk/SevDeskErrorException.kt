package me.rumbugen.sevdesk

import io.ktor.http.HttpStatusCode


class SevDeskErrorException(httpStatus: HttpStatusCode, override val message: String?, code: Int?) : Exception("${httpStatus.value}: $message ($code)")
class SevDeskInvalidResponse(override val message: String?) : Exception(message)