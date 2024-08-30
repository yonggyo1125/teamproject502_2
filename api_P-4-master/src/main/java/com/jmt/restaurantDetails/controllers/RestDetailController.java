package com.jmt.restaurantDetails.controllers;

import com.jmt.global.Utils;
import com.jmt.restaurant.services.RestaurantInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurantList")
@RequiredArgsConstructor
public class RestDetailController {

    private final RestaurantInfoService restaurantInfoService;

    @GetMapping("/details/{id}")
    public String view(@PathVariable("id") Long rstrId) {



        return "restaurantList/details/" + rstrId;
    }

}

