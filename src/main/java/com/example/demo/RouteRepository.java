package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: dazhi
 * @version: 1.0
 */
@Component
public class RouteRepository {
    public List<Map<String, Object>> list = new ArrayList<>();

    @PostConstruct
    public void init() {
        Map<String, Object> map = Map.of("routeId", "1",
                "predicateName", "Path",
                "predicateArgs", "/123456/**",
                "uri", "http://localhost:8081",
                "filters", List.of(
                        new FilterInfo("StripPrefix", "1"),
                        new FilterInfo("PrefixPath", "/gz-bigdata-service-platform")
//                        new FilterInfo("ModifyResponseBody", "/gz-bigdata-service-platform")
                ));

//        Map<String, Object> map2 = Map.of("routeId", "2",
//                "predicateName", "Path",
//                "predicateArgs", "/111/**",
//                "uri", "http://localhost:8082",
//                "filters", List.of(new FilterInfo("StripPrefix", "1")));
//                ,
//                "filters", List.of(new FilterInfo("StripPrefix", "1"), new FilterInfo("PrefixPath", "/gz-bigdata-service-platform")));

        list.add(map);
//        list.add(map2);
    }

    public List<Map<String, Object>> getListAll() {
        return list;
    }

    public static class FilterInfo {
        public String filterName;
        public String filterArgs;

        public FilterInfo(String filterName, String filterArgs) {
            this.filterName = filterName;
            this.filterArgs = filterArgs;
        }
    }
}
