package com.jmt.reservation.controllers;

import com.jmt.global.CommonSearch;
import lombok.Data;

@Data
public class ReservationSearch extends CommonSearch {
    private int page = 1;
    private int limit = 20;

    private String sopt; // 검색 조건
    private String skey; // 검색 키워드

    private String rName;

}
