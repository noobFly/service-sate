package com.noob.state.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Service extends Meta {

    private String providerCode;

    public Service(String code, String name,  String providerCode) {
        super(code, name);
        this.providerCode = providerCode;

    }

}
