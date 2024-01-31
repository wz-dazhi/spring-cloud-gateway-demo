//package com.example.demo;
//
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @author: dazhi
// * @version: 1.0
// */
//@Component
//public class ResponseBodyConverterFilter implements GlobalFilter, Ordered {
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        return chain.filter(exchange).then(Mono.defer(() -> {
//            ServerHttpResponse response = exchange.getResponse();
//            // 只在响应体没有被写入时转换编码，避免重复转换
//            if (!response.isCommitted()) {
//                return response.bufferFactory()
//                        .wrap(new String(response.getBody().blockLast().getData(), response.getHeaders().getContentType().getCharset()))
//                        .writeWith(response.getBody());
//            }
//            return Mono.empty();
//        }));
//    }
//
//    @Override
//    public int getOrder() {
//        // 确保这个过滤器在响应写入之前执行
//        return -1;
//    }
//}
