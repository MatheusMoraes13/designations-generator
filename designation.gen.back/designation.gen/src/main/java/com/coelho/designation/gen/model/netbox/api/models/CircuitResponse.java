package com.coelho.designation.gen.model.netbox.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@AllArgsConstructor
public class CircuitResponse {
    private String cid;
    private Object tenant;
    private String description;
    private String comments;
    private CustomFields customFields;
}
