package com.coelho.designation.gen.model.netbox.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter @Setter
@AllArgsConstructor
public class NetboxApiResponse {

    private String count;
    private String next;
    private String previous;
    private List<CircuitResponse> results;
}