package com.jmt.reservation.controllers;

import com.jmt.global.rests.JSONData;
import com.jmt.reservation.entities.Reservation;
import com.jmt.reservation.services.ReservationAdminService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reservation/admin")
public class ReservationAdminContoller {
    private final ReservationAdminService service;
    private final JPAQueryFactory queryFactory;


    @GetMapping("/list")// 목록 조회
    public JSONData getList() {
        List<Reservation> data = service.getList();

        return new JSONData(data);
    }
}