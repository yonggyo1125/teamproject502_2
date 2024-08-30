package com.jmt.reservation.services;

import com.jmt.global.ListData;
import com.jmt.global.Pagination;
import com.jmt.reservation.controllers.ReservationSearch;
import com.jmt.reservation.entities.QReservation;
import com.jmt.reservation.entities.Reservation;
import com.jmt.reservation.exceptions.ReservationNotFoundException;
import com.jmt.reservation.repositories.ReservationRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationInfoService {

    private final HttpServletRequest request; // 검색어 반영된 쿼리스트링 값
    private final ReservationRepository reservationRepository;
    private final JPAQueryFactory queryFactory;
    /**
     * 목록 조회
     * @param search
     * @return
     */
    public ListData<Reservation> getList(ReservationSearch search) {
        int page = Math.max(search.getPage(), 1); // 페이지가 0이거나 음수이면 1이 나오도록 설정
        int limit = search.getLimit(); // 한페이지당 보여줄 레코드 개수
        limit = limit < 1 ? 10 : limit;
        int offset = (page -1) * limit; // 레코드 시작 위치 구하기

        /* 검색 처리 S */
        QReservation reservation = QReservation.reservation;
        BooleanBuilder andBuilder = new BooleanBuilder();

        // 키워드 검색
        String sopt = search.getSopt(); // 검색 옵션 All - 통합 검색
        String skey = search.getSkey();  // 검색 키워드를 통한 검색 ex) 음식분류, 옵션 검색
        String rName = search.getRName(); // rName - 예약한 식당명

        sopt = StringUtils.hasText(sopt) ? sopt : "All"; // 통합검색이 기본
        // 키워드가 있을 때 조건별 검색
        if (StringUtils.hasText(skey) && StringUtils.hasText(skey.trim())) {
            /**
             * sopt
             * ALL - 통합 검색 - RNAME, RADDRESS, RTEL
             * RNAME, RTEL, RADDRESS
             */
            sopt = sopt.trim();
            skey = skey.trim();

            BooleanExpression condition = null;
            if(sopt.equals("ALL")) {
                // 통합 검색
                condition = reservation.rName.concat(reservation.rAddress).concat(reservation.rTel).contains(skey);
            } else if (sopt.equals("RNAME")) { // 식당명
                condition = reservation.rName.contains(skey);

            } else if (sopt.equals("RADDRESS")) { // 식당 주소
                condition = reservation.rAddress.contains(skey);

            } else if (sopt.equals("RTEL")) { // 식당 연락처
                skey = skey.replaceAll("-", ""); // 숫자만 남긴다
                condition = reservation.rTel.contains(skey);
            }

            if (condition != null) {
                andBuilder.and(condition);
            }
        }
        /* 검색 처리 E */

        // 페이징 데이터
        long total = reservationRepository.count(andBuilder); // 조회된 전체 갯수

        Pagination pagination = new Pagination(page, (int)total, 10, limit, request);

        List<Reservation> items = reservationRepository.findAll();

        return new ListData<>(items,pagination);
    }

    public Reservation get(Long orderNo) {
        Reservation item = reservationRepository.findById(orderNo).orElseThrow(ReservationNotFoundException::new);

        // 추가 데이터 처리
        addInfo(item);

        return item;
    }

    private void addInfo(Reservation item) {
        int persons = Math.max(item.getPersons(), 1);
        int price = item.getPrice();

        int totalPayPrice = price * persons;
        item.setTotalPayPrice(totalPayPrice);
    }
}
