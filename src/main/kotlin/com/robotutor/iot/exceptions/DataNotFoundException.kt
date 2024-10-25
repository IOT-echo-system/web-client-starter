package com.robotutor.iot.exceptions

class DataNotFoundException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
