package com.coelho.designation.gen.service;

import com.coelho.designation.gen.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "netbox", url = "${netbox.url}", configuration = FeignClientConfig.class)
public interface NetboxCircuitsService {

    @GetMapping("/api/circuits/circuits/?id=190")
    String getCircuits();
}
