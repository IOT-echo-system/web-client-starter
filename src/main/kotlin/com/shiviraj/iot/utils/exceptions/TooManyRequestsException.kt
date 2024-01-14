package com.shiviraj.iot.userService.exceptions

class TooManyRequestsException(
    serviceError: ServiceError,
    details: Map<String, Any> = emptyMap()
) : BaseException(serviceError, details)
