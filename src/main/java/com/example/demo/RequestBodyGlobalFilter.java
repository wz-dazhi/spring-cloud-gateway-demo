package com.example.demo;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: dazhi
 * @version: 1.0
 */
@Component
public class RequestBodyGlobalFilter implements Ordered, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getMethod() == HttpMethod.GET) {
            return chain.filter(exchange);
        } else {

            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> { //设定返回值并处理
                        DataBufferUtils.retain(dataBuffer); //设定存储空间
                        Flux<DataBuffer> cachedFlux = Flux//读取Flux中所有数据并且保存
                                .defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator( //得到ServerHttpRequest
                                exchange.getRequest()) {
                            @Override //重载getBody方法 让其从我设定的缓存获取
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }
                        };

                        //放行 并且设定exchange为我重载后的
                        ModifiedResponseDecorator decoratedResponse = new ModifiedResponseDecorator(exchange, new ModifyResponseBodyGatewayFilterFactory.Config().
                                setRewriteFunction(String.class, String.class, (ex, responseData) ->  {
                                    System.out.println("resp body: " + responseData);
                                    System.out.println("resp code: " + ex.getResponse().getStatusCode());
                                    System.out.println("resp headers: " + ex.getResponse().getHeaders());
                                    return Mono.just(responseData);
                                }));
                        StopWatch sw = new StopWatch(exchange.getRequest().getId());
                        sw.start();
                        return chain.filter(exchange.mutate().request(mutatedRequest).response(decoratedResponse).build())
                                .doOnError(t -> {
                                    ServerHttpResponse resp = decoratedResponse.getDelegate();
//                                    System.out.println("resp body: " + responseData);
                                    System.out.println("on error resp code: " + resp.getStatusCode());
                                    System.out.println("on error resp headers: " + resp.getHeaders());
                                    System.out.println("on error t: " + t);
                                }).doOnTerminate(() -> {
                                    sw.stop();
                                    System.out.println("on terminate..." + sw.getTotalTimeMillis());
                                    System.out.println("on terminate cost pretty print: " + sw.prettyPrint(TimeUnit.MILLISECONDS));
                                }).doAfterTerminate(() -> {
                                    System.out.println("on after terminate...");
                                });
//                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    });
        }
    }

    @Override
    public int getOrder() {
        return -2;
    }

//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        return exchange.getRequest().getHeaders().getContentType() != null ?
//                DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
//                    DataBufferUtils.retain(dataBuffer);
//                    Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
//                    ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
//                        @Override
//                        public Flux<DataBuffer> getBody() {
//                            return cachedFlux;
//                        }
//                    };
//                    String requestBody = resolveBodyFromRequest(decoratedRequest.getBody());
//                    ServerHttpResponse originalResponse = exchange.getResponse();
//                    DataBufferFactory bufferFactory = originalResponse.bufferFactory();
//                    ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
//                        @Override
//                        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
//                            if (body instanceof Flux) {
//                                Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
//                                return super.writeWith(fluxBody.map(dataBuffer -> {
//                                    // probably should reuse buffers
//                                    byte[] content = new byte[dataBuffer.readableByteCount()];
//                                    dataBuffer.read(content);
//                                    //释放掉内存
//                                    DataBufferUtils.release(dataBuffer);
//                                    String responseBody = new String(content, Charset.forName("UTF-8"));
//                                    System.out.println("==req body: " + requestBody);
//                                    System.out.println("==resp body: " + responseBody);
//                                    byte[] uppedContent = new String(content, Charset.forName("UTF-8")).getBytes();
//                                    return bufferFactory.wrap(uppedContent);
//                                }));
//                            }
//                            // if body is not a flux. never got there.
//                            return super.writeWith(body);
//                        }
//                    };
//                    return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build());
//                }) : chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return -10;
//    }
//
//    /**
//     * spring cloud gateway 获取post请求的body体
//     *
//     * @param body
//     * @return
//     */
//    private String resolveBodyFromRequest(Flux<DataBuffer> body) {
//        AtomicReference<String> bodyRef = new AtomicReference<>();
//        // 缓存读取的request body信息
//        body.subscribe(dataBuffer -> {
//            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer());
//            DataBufferUtils.release(dataBuffer);
//            bodyRef.set(charBuffer.toString());
//        });
//        return bodyRef.get();
//
//    }

}
