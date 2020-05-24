package com.itmo.ncfs.integrations.shopping.dto;

import lombok.Data;

@Data
public class GetCustomerResponse {

    private Integer id;

    private String name;

    private String email;
}
