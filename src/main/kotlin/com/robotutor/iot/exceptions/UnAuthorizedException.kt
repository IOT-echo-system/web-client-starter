package com.robotutor.iot.exceptions

class UnAuthorizedException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
