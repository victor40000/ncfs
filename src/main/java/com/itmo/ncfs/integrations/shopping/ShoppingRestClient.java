package com.itmo.ncfs.integrations.shopping;

import com.itmo.ncfs.exceptions.ShoppingPortalException;
import com.itmo.ncfs.integrations.shopping.dto.GetCustomerResponse;
import com.itmo.ncfs.integrations.shopping.dto.GetProductResponse;
import com.itmo.ncfs.services.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class ShoppingRestClient {

    private final ConfigService configService;

    @Autowired
    public ShoppingRestClient(ConfigService configService) {
        this.configService = configService;
    }

    private static final String GET_CUSTOMER_ERROR = "Could not get information about customer with id %s from portal";
    private static final String GET_PRODUCT_ERROR = "Could not get information about product with id %s from portal";
    private static final String GET_CUSTOMER_URL = "/api/v1/shopping-portal/customers/%s";
    private static final String GET_PRODUCT_URL = "/api/v1/shopping-portal/products/%s";

    public GetCustomerResponse getCustomer(Integer id) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(configService.getGatewayAddress())
                .path(String.format(GET_CUSTOMER_URL, id))
                .build();
        try {
            ResponseEntity<GetCustomerResponse> response = restTemplate
                    .getForEntity(uriComponents.toUriString(), GetCustomerResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            throw new ShoppingPortalException(String.format(GET_CUSTOMER_ERROR, id));
        }
    }

    public GetProductResponse getProduct(Integer id) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(configService.getGatewayAddress())
                .path(String.format(GET_PRODUCT_URL, id))
                .build();
        try {
            ResponseEntity<GetProductResponse> response = restTemplate
                    .getForEntity(uriComponents.toUriString(), GetProductResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            throw new ShoppingPortalException(String.format(GET_PRODUCT_ERROR, id));
        }
    }

}
