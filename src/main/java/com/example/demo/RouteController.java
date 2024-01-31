package com.example.demo;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: dazhi
 * @version: 1.0
 */
@RestController
public class RouteController {
    @Resource
    private GatewayServiceHandler gatewayServiceHandler;
    @Resource
    private RouteRepository routeRepository;

    @PostMapping("/add")
    public String add(@RequestBody Map<String, Object> map) {
//        gatewayRouteService.addRouteInfo(vo);'
        routeRepository.list.add(map);
        gatewayServiceHandler.loadRouteConfig();
        return "新增路由成功";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String id) {
//        TGatewayRoutesDO routesDO = routeRepository.getByNId(Long.parseLong(id));
//        gatewayRouteService.deleteRoute(Long.parseLong(id));
        gatewayServiceHandler.deleteRoute(id);
        return "删除路由成功";
    }

//    @PostMapping("/update")
//    public String update(@RequestBody @Valid RouteAddInfoVo vo) {
////        gatewayRouteService.updateRouteInfo(vo);
//        gatewayServiceHandler.loadRouteConfig();
//        return "更新路由成功";
//    }
}
