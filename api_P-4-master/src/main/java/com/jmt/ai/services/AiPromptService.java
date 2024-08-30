package com.jmt.ai.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmt.ai.controllers.RequestMessage;
import com.jmt.global.services.ConfigInfoService;
import com.jmt.restaurant.entities.QFoodMenu;
import com.jmt.restaurant.entities.QRestaurant;
import com.jmt.restaurant.entities.Restaurant;
import com.jmt.restaurant.repositories.FoodMenuRepository;
import com.jmt.restaurant.repositories.RestaurantRepository;
import com.jmt.restaurant.services.RestaurantInfoService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AiPromptService {
    private final ConfigInfoService infoService;
    private final ObjectMapper om;
    private final RestTemplate restTemplate;
    private final RestaurantRepository restaurantRepository; // 카운트 할 때 필요
    private final JPAQueryFactory queryFactory;
    private final RestaurantInfoService restaurantInfoService;
    private final FoodMenuRepository foodRepository;
    private final EntityManager em;

    public String prompt(String message) {
        Map<String, String> config = infoService.getApiConfig();
        if (config == null || !StringUtils.hasText(config.get("huggingfaceAccessToken"))) {
            return null;
        }

        String token = config.get("huggingfaceAccessToken").trim();

        String url = "https://api-inference.huggingface.co/models/mistralai/Mistral-Nemo-Instruct-2407/v1/chat/completions";

        Map<String, String> data = new HashMap<>();
        data.put("role", "user");
        data.put("content", message);
        RequestMessage params = new RequestMessage(List.of(data));
        try {
            String json = om.writeValueAsString(params);

            HttpHeaders headers = new HttpHeaders();

            System.out.println("token:" + token);
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(json, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(URI.create(url), request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> data2 = om.readValue(response.getBody(), new TypeReference<>(){});

                List<Map<String, Object>> data3 = (List<Map<String, Object>>)data2.get("choices");

                Map<String, String> data4 = (Map<String, String>)data3.get(0).get("message");

                String message2 = data4.get("content");
                System.out.println(message2);

                return message2;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Restaurant onePick(String message) {

        TypedQuery<Restaurant> query = em.createQuery("SELECT r FROM Restaurant r left join r.foods m where r.rstrLnnoAdres like :k1 and m.menuNm = :k2", Restaurant.class);
        return null;
    }

    public Restaurant onePickRestaurant(String message) {

        QRestaurant restaurant = QRestaurant.restaurant;
        QFoodMenu foodMenu = QFoodMenu.foodMenu;

        BooleanBuilder restBuilder = new BooleanBuilder();
        AtomicReference<String> keyAddr = new AtomicReference<>("");
        AtomicReference<String> keyMenu = new AtomicReference<>("");
        String keyEtc = "";
        AtomicBoolean sqlFood = new AtomicBoolean(false);
        AtomicBoolean existAddr = new AtomicBoolean(false);
        AtomicBoolean existMenu = new AtomicBoolean(false);

        Restaurant data;

        message = message
                .replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z\\s]", "")
                .replace("앞", "")
                .replace("역전", "")
                .replace("가게", "")
                .replace("맛집", "")
                .replace("내 근처", "신촌")
                .replace("이 근처", "신촌")
                .replace("내 주변", "신촌")
                .replace("여기", "신촌")
                .replace("부근", "")
                .replace("근처", "")
                .replace("알려줘", "")
                .replace("말해줘", "")
                .replace("알려주세요", "")
                .replace("뭐", "")
                .replace("점심 ", "")
                .replace("저녁 ", "")
                .replace("먹지", "")
                .replace("먹을지", "")
                .replace("점심 ", "")
                .replace("가르쳐", "")
                .replace("가리켜", "")
                .replace("맛있는", "")
                .replace("가 ", " ")
                .replace("이 ", " ")
                .replace("을 ", " ")
                .replace("를 ", " ")
                .replace("에서 ", " ")
                .replace("옆 ", " ")
                .replace("식당", "")
                .replace("집", "");

        List<String> arr = Arrays.asList(message.split(" "));

        arr.forEach(a -> {
            if(!StringUtils.hasText(a)) return;
            if(existAddr.get() && existMenu.get()) return;

            System.out.println("a => " + a);
            boolean isEtc = true;  // 지명, 메뉴명 아닌 키워드

            BooleanBuilder b = new BooleanBuilder();
            b.and(restaurant.rstrLnnoAdres.concat(restaurant.rstrRdnmAdr).contains(a));
            long cntAddr = restaurantRepository.count(b);

            if (cntAddr > 20) { // a : 주소
                isEtc = false;
                restBuilder.and(restaurant.rstrLnnoAdres.concat(restaurant.rstrRdnmAdr).contains(a));
                keyAddr.set("%" + a + "%");

                existAddr.set(true);

                System.out.println("cntAddr => " + cntAddr);
            } else {

                // 대표 메뉴에 있는지
                BooleanBuilder b1 = new BooleanBuilder();
                b1.and(restaurant.reprsntMenuNm.contains(a));
                long cntRpr = restaurantRepository.count(b1);
                System.out.println("cntRpr => " + cntRpr);

                // 메뉴 리스트 에 있는지
                BooleanBuilder b2 = new BooleanBuilder();
                b2.and(foodMenu.menuNm.contains(a));
                long cntMenu = foodRepository.count(b2);
                System.out.println("cntMenu => " + cntMenu);

                if (cntMenu > 50) { // a : 메뉴명 있음
                    isEtc = false;
                    existMenu.set(true);
                    sqlFood.set(true);

                    keyMenu.set("%" + a + "%");

                } else if (cntRpr > 0) { // a : 대표 메뉴에 있음
                    isEtc = true;
                }

            }
            if (isEtc) {

                BooleanBuilder b3 = new BooleanBuilder();
                b3.and(restaurant.rstrNm.concat(restaurant.rstrIntrcnCont).contains(a));

                long cntEtc = restaurantRepository.count(b3);
                if (cntEtc >= 1) {
                    restBuilder.and(restaurant.rstrNm.concat(restaurant.rstrIntrcnCont).contains(a));
                    sqlFood.set(false);
                    existMenu.set(true);

                    keyMenu.set("%" + a + "%");

                }
            }
        });

        long total = 0;

        System.out.println("keyAddr => " + keyAddr.get() + " keyMenu => " + keyMenu.get()  );
        if(sqlFood.get()) { // JPA 쿼리 수행
            total = restaurantRepository.getCountBy(keyAddr.get(), keyMenu.get());
        }else {   // Querydsl 쿼리 수행
            total = restaurantRepository.count(restBuilder);
        }

        System.out.println("total:" + total);

        if (total == 0) { // 없으면 랜덤 으로
            while (true) {
                Long rstrId = Long.valueOf((long) (new Random()).nextInt(2000) + 1);
                try {
                    data = restaurantInfoService.get(rstrId);  //3693L
                }catch (Exception e){
                    continue;
                }
                if (data != null) {
                    break;
                }
            }
            System.out.println("===== 없어서 랜덤 pick =====");
            return data;
        }
        if(sqlFood.get()) { // JPA 쿼리 수행
            List<Restaurant> items = restaurantRepository.getRestaurants(keyAddr.get(), keyMenu.get());
            int row = (new Random()).nextInt(items.size());

            data = items.get(row);
            System.out.println("===== JPA 쿼리 pick =====");

        } else { // queryDsl 수행
            data = queryFactory.selectFrom(restaurant)
                    .where(restBuilder)
                    .fetchFirst();

            System.out.println("===== queryDsl pick =====");
        }
        return data;
    }

}