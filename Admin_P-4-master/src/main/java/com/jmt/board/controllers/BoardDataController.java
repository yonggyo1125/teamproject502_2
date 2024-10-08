package com.jmt.board.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmt.board.entities.BoardData;
import com.jmt.global.Utils;
import com.jmt.global.exceptions.UnAuthorizedException;
import com.jmt.global.rests.JSONData;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
@Controller
@RequiredArgsConstructor
@RequestMapping("/board/posts")
public class BoardDataController
{

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper om;
    private final Utils utils;

    @GetMapping()
    public String dataList(Model model) throws JsonProcessingException {

        String url = utils.url("/board_data/list", "api-service");
        System.out.println("url:" + url );

        String token = utils.getToken();
        if (!StringUtils.hasText(token)) {
            throw new UnAuthorizedException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<JSONData> request = new HttpEntity<>(headers);


        ResponseEntity<JSONData> response = restTemplate.exchange(URI.create(url), HttpMethod.GET, request, JSONData.class);

        System.out.println("==========response==============");
        System.out.println(response);
        if(!response.getStatusCode().toString().contains("200")) {
            throw new RuntimeException("api Server returned: " + response.getStatusCode());
        }
        String jsonString = om.writeValueAsString(response.getBody().getData());
        System.out.println("jsonString :" + jsonString);

        List<BoardData> items = om.readValue(jsonString, new TypeReference<List<BoardData>>() {});
        System.out.println(items);

        model.addAttribute("posts", items);
        return "board/posts";
    }
}
