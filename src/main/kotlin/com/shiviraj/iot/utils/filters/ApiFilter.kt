package com.shiviraj.iot.utils.filters

import com.shiviraj.iot.loggingstarter.logOnSuccess
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime


@Component
class ApiFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val startTime = LocalDateTime.now()
        return chain.filter(exchange)
            .publishOn(Schedulers.boundedElastic())
            .contextWrite { it.put(ServerWebExchange::class.java, exchange) }
            .contextWrite { it.put("startTime", startTime) }
            .doFinally {
                Mono.just("")
                    .logOnSuccess("Successfully send api response")
                    .contextWrite { it.put(ServerWebExchange::class.java, exchange) }
                    .contextWrite { it.put("startTime", startTime) }
                    .subscribe()
            }
    }
}

