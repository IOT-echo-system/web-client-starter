package com.robotutor.iot.exceptions

class BadDataException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
