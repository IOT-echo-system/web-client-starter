package com.robotutor.iot

import com.mongodb.ConnectionString
import com.robotutor.iot.filters.WebApiFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.web.server.WebFilter

@Configuration
class UtilsConfiguration(private val environment: Environment) {

    @Bean
    @Primary
    fun reactiveMongoTemplate(): ReactiveMongoTemplate {
        val uri = environment.getRequiredProperty("spring.data.mongodb.uri")
        return ReactiveMongoTemplate(SimpleReactiveMongoDatabaseFactory(ConnectionString(uri)))
    }

    @Bean
    fun customApiFilter(): WebFilter {
        return WebApiFilter()
    }
}
