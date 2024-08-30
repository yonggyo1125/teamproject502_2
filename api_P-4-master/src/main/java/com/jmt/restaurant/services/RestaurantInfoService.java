package com.jmt.restaurant.services;

import com.jmt.global.CommonSearch;
import com.jmt.global.ListData;
import com.jmt.global.Pagination;
import com.jmt.restaurant.controllers.RestaurantSearch;
import com.jmt.restaurant.entities.QRestaurant;
import com.jmt.restaurant.entities.Restaurant;
import com.jmt.restaurant.entities.RestaurantImage;
import com.jmt.restaurant.exceptions.RestaurantNotFoundException;
import com.jmt.restaurant.repositories.RestaurantRepository;
import com.jmt.wishlist.constants.WishType;
import com.jmt.wishlist.services.WishListService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantInfoService {

    private final HttpServletRequest request; // 검색어 반영된 쿼리스트링 값
    private final RestaurantRepository repository; // 카운트 할 때 필요
    private final JPAQueryFactory queryFactory;
    private final WishListService wishListService;
    private final RestaurantImageService imageService;
    /**
     * 목록 조회
     * @param search
     * @return
     */
    public ListData<Restaurant> getList(RestaurantSearch search) {
        int page = Math.max(search.getPage(), 1); // 페이지가 0이거나 음수이면 1이 나오도록 설정
        int limit = search.getLimit(); // 한페이지당 보여줄 레코드 개수
        limit = limit < 1 ? 10 : limit;
        int offset = (page -1) * limit; // 레코드 시작 위치 구하기

        /* 검색 처리 S */
        QRestaurant restaurant = QRestaurant.restaurant;
        BooleanBuilder andBuilder = new BooleanBuilder();

        // 키워드 검색
        String sopt = search.getSopt(); // 검색 옵션 All - 통합 검색
        String skey = search.getSkey();  // 검색 키워드를 통한 검색 ex) 음식분류, 옵션 검색
        String areaNm = search.getAreaNm(); // areaNm - 지역명(서울특별시+구)
        String dbsnsStatmBzcndNm = search.getDbsnsStatmBzcndNm(); // dbsnsStatmBzcndNm - 업종명

        // 지역명 검색
        if (StringUtils.hasText(areaNm)) {
            andBuilder.and(restaurant.areaNm.eq(areaNm));
        }

        // 업종별 검색
        if (StringUtils.hasText(dbsnsStatmBzcndNm)) {
            andBuilder.and(restaurant.dbsnsStatmBzcndNm.eq(dbsnsStatmBzcndNm));
        }

        sopt = StringUtils.hasText(sopt) ? sopt : "All"; // 통합검색이 기본
        // 키워드가 있을 때 조건별 검색
        if (StringUtils.hasText(skey) && StringUtils.hasText(skey.trim())) {
            /**
             * sopt
             * ALL - 통합 검색 - title, tel, address, category
             * TITLE, TEL, ADDRESS, CATEGOTY
             */
            sopt = sopt.trim();
            skey = skey.trim();

            BooleanExpression condition = null;
            if(sopt.equals("ALL")) {
                // 통합 검색
                condition = restaurant.rstrNm.concat(restaurant.rstrTelNo).concat(restaurant.rstrRdnmAdr).concat(restaurant.dbsnsStatmBzcndNm).contains(skey);
            } else if (sopt.equals("TITLE")) { // 레스토랑 명
                condition = restaurant.rstrNm.contains(skey);

            } else if (sopt.equals("TEL")) { // 연락처
                skey = skey.replaceAll("-", ""); // 숫자만 남긴다
                condition = restaurant.rstrTelNo.contains(skey);

            } else if (sopt.equals("ADDRESS")) { // 도로명 주소 - rstrRdnmAdr
                condition = restaurant.rstrRdnmAdr.contains(skey);

            } else if (sopt.equals("CATEGORY")) { // 업종명 - dbsnsStatmBzcndNm
                condition = restaurant.dbsnsStatmBzcndNm.contains(skey);

            }

            if (condition != null) {
                andBuilder.and(condition);
            }
        }
        /* 검색 처리 E */

        // 검색 데이터 처리
        List<Restaurant> items = queryFactory.selectFrom(restaurant)
                .leftJoin(restaurant.images)
                .fetchJoin()
                .where(andBuilder) // 검색 조건 후에 추가
                .offset(offset)
                .limit(limit)
                .orderBy(restaurant.createdAt.desc()) // 정렬 조건 후에 추가
                .fetch();

        items.forEach(this::addInfo);

        // 페이징 데이터
        long total = repository.count(andBuilder); // 조회된 전체 갯수

        Pagination pagination = new Pagination(page, (int)total, 10, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 식당 개별 정보 조회
     * @param rstrId
     * @return
     */
    public Restaurant get(Long rstrId) {
        // 2차 가공 필요
        Restaurant item = repository.findById(rstrId).orElseThrow(RestaurantNotFoundException::new);

        // 식당 이미지 바로 가져오기

        // 추가 데이터 처리 -> 리뷰

        // 추가 데이터 처리
        addInfo(item);

        return item;
    }
    // 예약 가능한 정보, 제한된 상품 정보, 중복 예약 방지

    /**
     *     식당 위시리스트
     *
     *     찜하기 목록
     *
     *      @return
      */
    public ListData<Restaurant> getWishList(CommonSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;


        List<Long> rstrIds = wishListService.getList(WishType.RESTAURANT);
        if (rstrIds == null || rstrIds.isEmpty()) {

            return new ListData<>();

        }

        QRestaurant restaurant = QRestaurant.restaurant;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(restaurant.rstrId.in(rstrIds));

        List<Restaurant> items = queryFactory.selectFrom(restaurant)
                .leftJoin(restaurant.images)
                .fetchJoin()
                .where(andBuilder)
                .offset(offset)
                .limit(limit)
                .orderBy(restaurant.createdAt.desc())
                .fetch();

        items.forEach(this::addInfo);

        long total = repository.count(andBuilder); // 조회된 전체 갯수

        Pagination pagination = new Pagination(page, (int)total, 10, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 추가 데이터 처리
     * 1. 예약 가능 일
     * 2. 예약 가능 요일
     * 3. 예약 가능 시간대
     * @param item
     */
    private void addInfo(Restaurant item) {
        // 운영 정보로 예약 가능 데이터 처리 S
        String operInfo = item.getBsnsTmCn();
        try {
            if (operInfo == null || !StringUtils.hasText(operInfo.trim())) {
                operInfo = "매일 12:00~22:00";
            }

            if (operInfo != null && StringUtils.hasText(operInfo.trim())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                Map<String, List<LocalTime>> availableTimes = new HashMap<>();
                boolean[] yoils = new boolean[7]; // 0~6 true, false
                for (String oper : operInfo.split(",\\s*")) {
                    String[] _oper = oper.split("\\s+");
                    String yoil = _oper[0];
                    String time = _oper[1];
                    System.out.printf("yoil=%s, time=%s%n", yoil, time);

                    if (yoil.equals("평일")) {
                        for (int i = 1; i < 6; i++) {
                            yoils[i] = true;
                        }
                    } else if (yoil.equals("매일")) {
                        for (int i = 0; i < yoils.length; i++) {
                            yoils[i] = true;
                        }
                    } else if (yoil.equals("토요일")) {
                        yoils[6] = true;
                    } else if (yoil.equals("일요일")) {
                        yoils[0] = true;
                    } else if (yoil.equals("주말")) {
                        yoils[0] = yoils[6] = true;
                    }

                    // 예약 가능 시간대 S
                    String[] _time = time.split("~");
                    LocalTime sTime = LocalTime.parse(_time[0], formatter);
                    LocalTime eTime = LocalTime.parse(_time[1], formatter);


                    List<LocalTime> _avaliableTimes = new ArrayList<>();
                    Duration du = Duration.between(sTime, eTime);
                    int hours = (int)du.getSeconds() / (60 * 60);

                    for (int i = 0; i <= hours; i++) {
                        LocalTime t = sTime.plusHours(i);
                        // 예약 가능 시간에 + 1시간이 종료 시간을 지난 경우는 X
                        if (t.plusHours(1L).isAfter(eTime)) {
                            continue;
                        }
                        _avaliableTimes.add(t);
                    }

                    availableTimes.put(yoil, _avaliableTimes);
                    // 예약 가능 시간대 E
                }

                // 예약 가능 시간대
                item.setAvailableTimes(availableTimes);

                item.setAvailableWeeks(yoils);

                List<LocalDate> availableDates = new ArrayList<>();
                LocalDate startDate = LocalDate.now().plusDays(1L);
                LocalDate endDate = startDate.plusMonths(1L).minusDays(1L);

                Period period = Period.between(startDate, endDate);
                int days = period.getDays() + 1;

                for (int i = 0; i <= days; i++) {
                    LocalDate date = startDate.plusDays(i);
                    int yoil = date.getDayOfWeek().getValue() % 7;
                    if (yoils[yoil]) { // 영업 가능 요일인 경우
                        availableDates.add(date);
                    }
                }
                item.setAvailableDates(availableDates);
            } // endif

            // 운영 정보로 예약 가능 데이터 처리 E
        } catch (Exception e) {
            System.out.println(operInfo);
        }

        // 이미지가 없는 식당 이미지 업데이트 S
        List<RestaurantImage> images = item.getImages();
        if (images == null || images.isEmpty()) {
            imageService.update(item.getRstrId(), item);
        }
        // 이미지가 없는 식당 이미지 업데이트 E
    }
}