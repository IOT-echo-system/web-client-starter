import com.shiviraj.iot.loggingstarter.logOnError
import com.shiviraj.iot.loggingstarter.logOnSuccess
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class WebClientWrapper(private val webClient: WebClient) {
    fun <T> get(
        baseUrl: String,
        path: String,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
    ): Mono<T> {
        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return webClient.get()
            .uri(url)
            .headers { h ->
                headers.map {
                    h.set(it.key, it.value)
                }
            }
            .retrieve()
            .bodyToMono(returnType)
            .logOnSuccess(
                message = "GET request to Service successful",
                searchableFields = mapOf(
                    "uriWithParams" to url
                )
            ).logOnError(
                errorCode = "",
                errorMessage = "GET request to Service failed",
                searchableFields = mapOf(
                    "uriWithParams" to url
                )
            )
            .contextWrite {
                it.put("startTime", LocalDateTime.now())
            }
    }

    fun <T> post(
        baseUrl: String,
        path: String,
        body: Any,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
    ): Mono<T> {

        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return webClient
            .post().uri(url)
            .headers { h ->
                headers.map {
                    h.set(it.key, it.value)
                }
            }.bodyValue(body)
            .retrieve()
            .bodyToMono(returnType)
            .logOnSuccess(
                message = "POST request to Service successful",
                searchableFields = mapOf(
                    "uriWithParams" to url
                ),
            ).logOnError(
                errorCode = "",
                errorMessage = "POST request to Service failed",
                searchableFields = mapOf(
                    "uriWithParams" to url
                )
            )
            .contextWrite {
                it.put("startTime", LocalDateTime.now())
            }
    }

    private fun createUrlForRequest(
        baseUrl: String,
        path: String,
        uriVariables: Map<String, Any>,
        queryParams: MultiValueMap<String, String>
    ) = baseUrl + UriComponentsBuilder
        .fromPath(path)
        .uriVariables(uriVariables)
        .queryParams(queryParams)
        .build()
        .toUriString()
}
