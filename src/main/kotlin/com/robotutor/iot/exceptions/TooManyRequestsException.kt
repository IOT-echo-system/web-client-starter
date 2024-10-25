package com.robotutor.iot.exceptions

class TooManyRequestsException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
