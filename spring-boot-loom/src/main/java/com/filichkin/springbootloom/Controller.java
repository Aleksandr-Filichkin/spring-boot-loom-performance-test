package com.filichkin.springbootloom;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class Controller {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String host = "http://test:7000/address/";

    @GetMapping("/address/{timeout}")
    String getAddress(@PathVariable long timeout) throws URISyntaxException {
        URI uri = new URI(host + timeout);
        return restTemplate.getForObject(uri, String.class);
    }

}
