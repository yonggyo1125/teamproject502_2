package com.jmt.detail.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/detail")
@Slf4j
@RequiredArgsConstructor
public class detailController {

    @GetMapping("/view")
    public String view() {
        log.info("detail-view-check!!!!");

        return "매장상세 페이지 확인";
    }
}
