package com.example.demo;

import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author: dazhi
 * @version: 1.0
 */
@Component
public class GatewayServiceHandler implements ApplicationEventPublisherAware, CommandLineRunner {

    @Autowired
    private RouteMap routeMap;

    @Autowired
    private RouteRepository routeRepository;

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void run(String... args) {
        this.loadRouteConfig();
    }

    public void loadRouteConfig() {
        List<Map<String, Object>> lists = routeRepository.getListAll();
        lists.forEach(r -> {
            RouteDefinition route = new RouteDefinition();
            PredicateDefinition predicate = new PredicateDefinition();
            Map<String, String> predicateParams = new HashMap<>(2);


            //设置Id
            route.setId(String.valueOf(r.get("routeId")));
            try {
                route.setUri(new URI((String) r.get("uri")));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }


            predicate.setName((String) r.get("predicateName"));
            predicateParams.put("pattern", (String) r.get("predicateArgs"));
            predicate.setArgs(predicateParams);
            if (!StringUtils.isBlank(predicate.getName())) {
                route.setPredicates(Arrays.asList(predicate));
            }

            List<Object> filters = (List<Object>) r.get("filters");
            List<FilterDefinition> filterDefinitions = new ArrayList<>(filters != null ? filters.size() : 0);
            if (filters != null) {
                FilterDefinition filter = null;
                for (Object f : filters) {
                    if (f instanceof RouteRepository.FilterInfo ff) {
                        filter = new FilterDefinition();
                        Map<String, String> filterParams = new HashMap<>(2);
                        filter.setName(ff.filterName);
                        filterParams.put("_genkey_0", ff.filterArgs);
                        filter.setArgs(filterParams);
                        filterDefinitions.add(filter);
                    } else if (f instanceof Map<?, ?> m) {
                        filter = new FilterDefinition();
                        Map<String, String> filterParams = new HashMap<>(2);
                        filter.setName((String) m.get("filterName"));
                        filterParams.put("_genkey_0", (String) m.get("filterArgs"));
                        filter.setArgs(filterParams);
                        filterDefinitions.add(filter);
                    }
                }
            }

            route.setFilters(filterDefinitions);

            routeMap.save(Mono.just(route)).subscribe();
        });
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    public void deleteRoute(String routeId) {
        routeMap.delete(Mono.just(routeId)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

}