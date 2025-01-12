package com.robotutor.iot.utils

import com.robotutor.iot.exceptions.BaseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <T : Any> createMono(content: T): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.just(content)
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T : Any> createMonoError(exception: BaseException): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.error<T>(exception)
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T : Any> createFlux(content: List<T>): Flux<T> {
    return Flux.deferContextual { contextView ->
        Flux.fromIterable(content)
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T : Any> createFluxError(exception: BaseException): Flux<T> {
    return Flux.deferContextual { contextView ->
        Flux.error<T>(exception)
            .contextWrite { context -> context.putAll(contextView) }
    }
}
