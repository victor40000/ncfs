package com.itmo.ncfs.services;

import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    public String getGatewayAddress() {
        return "localhost:8443";
    }
}
