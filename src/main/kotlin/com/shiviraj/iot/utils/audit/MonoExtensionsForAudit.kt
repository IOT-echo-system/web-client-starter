package com.shiviraj.iot.utils.audit

import com.shiviraj.iot.mqtt.model.AuditEvent
import com.shiviraj.iot.mqtt.model.AuditMessage
import com.shiviraj.iot.mqtt.model.AuditStatus
import com.shiviraj.iot.mqtt.model.MqttTopicName
import com.shiviraj.iot.mqtt.service.MqttPublisher
import reactor.core.publisher.Mono
import reactor.util.context.ContextView
import java.time.LocalDateTime
import java.time.ZoneId


fun <T> Mono<T>.auditOnError(
    mqttPublisher: MqttPublisher,
    event: AuditEvent,
    metadata: Map<String, Any> = emptyMap(),
    userId: String? = null,
    accountId: String? = null,
    deviceId: String? = null,
): Mono<T> {
    return doOnEach { signal ->
        if (signal.isOnError) {
            val auditMessage = AuditMessage(
                status = AuditStatus.FAILURE,
                userId = userId ?: getUserId(signal.contextView),
                metadata = metadata,
                event = event,
                accountId = accountId ?: getAccountId(signal.contextView),
                deviceId = deviceId ?: getDeviceId(signal.contextView),
                timestamp = LocalDateTime.now(ZoneId.of("UTC"))
            )
            mqttPublisher.publish(MqttTopicName.AUDIT, auditMessage)
        }
    }
}


fun <T> Mono<T>.auditOnSuccess(
    mqttPublisher: MqttPublisher,
    event: AuditEvent,
    metadata: Map<String, Any> = emptyMap(),
    userId: String? = null,
    accountId: String? = null,
    deviceId: String? = null,
): Mono<T> {
    return doOnEach { signal ->
        if (signal.isOnNext) {
            val auditMessage = AuditMessage(
                status = AuditStatus.SUCCESS,
                userId = userId ?: getUserId(signal.contextView),
                metadata = metadata,
                event = event,
                accountId = accountId ?: getAccountId(signal.contextView),
                deviceId = deviceId ?: getDeviceId(signal.contextView),
                timestamp = LocalDateTime.now(ZoneId.of("UTC"))
            )
            mqttPublisher.publish(MqttTopicName.AUDIT, auditMessage)
        }
    }
}

private fun getDeviceId(contextView: ContextView): String? {
    return contextView.getOrDefault("deviceId", "missing-device-id")
}

private fun getAccountId(contextView: ContextView): String? {
    return contextView.getOrDefault("accountId", "missing-account-id")
}

private fun getUserId(contextView: ContextView): String {
    return contextView.getOrDefault("userId", "missing-user-id") ?: "missing-user-id"
}
