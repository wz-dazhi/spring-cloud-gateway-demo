package com.example.demo;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author: dazhi
 * @version: 1.0
 */
public class ErrorHandler extends DefaultErrorWebExceptionHandler {

    public ErrorHandler(ErrorAttributes errorAttributes,
                        WebProperties.Resources resources,
                        ErrorProperties errorProperties,
                        ApplicationContext applicationContext) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable throwable) {
        System.out.println("ErrorHandler id: " + request.exchange().getRequest().getId());
        System.out.println("ErrorHandler status: " + response.statusCode());
        System.out.println("ErrorHandler headers: " + response.headers());
        System.out.println("ErrorHandler t: " + throwable);
    }

    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> error = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        MediaType contentType = request.headers().contentType().orElse(MediaType.APPLICATION_JSON);
        return ServerResponse.status(getHttpStatus(error))
                .contentType(contentType)
                .body(BodyInserters.fromValue(error));
    }
}
