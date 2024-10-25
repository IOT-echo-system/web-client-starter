package com.robotutor.iot.exceptions

class AccessDeniedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
