package com.coelho.designation.gen.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

@Service
@FeignClient(name = "netbox", url = "${NETBOX_URL}")
public interface NetboxCircuitsService {
}
