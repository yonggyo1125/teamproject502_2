package com.jmt.restaurant.services;

import com.jmt.restaurant.entities.QRestaurantImage;
import com.jmt.restaurant.entities.Restaurant;
import com.jmt.restaurant.entities.RestaurantImage;
import com.jmt.restaurant.repositories.RestaurantImageRepository;
import com.jmt.restaurant.services.RestaurantInfoService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Setter
@RequiredArgsConstructor
public class RestaurantImageService {
    private final RestTemplate restTemplate;
    private final RestaurantImageRepository imageRepository;

    public String collect(Long rstrId, Restaurant restaurant) {
        try {

            String keyword = restaurant.getRstrRdnmAdr() + " " + restaurant.getRstrNm();

            String url = String.format("https://www.google.com/search?sca_esv=69c6459a1fa3319a&q=%s&udm=2&fbs=AEQNm0DmKhoYsBCHazhZSCWuALW8QMQUJa0B2f_Qpns8mPJGK_V3pO6T_3_HENtlLuoTnLnKZrWbxhALAvoRpUOHG-M2NXCstxg6Hz3ah2V3bzwV9AcUTlRfXUD7CgtSE3b3goMWMZLgSRsghBXwICSUo8Cs6iKSC1Sf6cL9BJYvc0e1Jx5ZPmpiL7tCSl3lW-pLUeXpb3-c&sa=X&ved=2ahUKEwjX36OMkIeIAxXtp1YBHeBiKUYQtKgLegQIExAB&biw=1197&bih=562&dpr=1.25", URLEncoder.encode(keyword, StandardCharsets.UTF_8));

            String data = restTemplate.getForObject(URI.create(url), String.class);
            System.out.println("data:" + data);
            data = data.split("<body\\>")[1];

            if (!StringUtils.hasText(data)) return null;

            Pattern pattern = Pattern.compile("<img.*src=['\"]?([^'\">]+)['\"]?[^>]*>", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()) {
                return matcher.group(1);
            }

        } catch (Exception e) { // 자동 수집으로 처리하므로 예외는 방지한다.
            e.printStackTrace();
        }

        return null;
    }

    public List<RestaurantImage> update(Long rstrId, Restaurant restaurant) {
        List<RestaurantImage> images = new ArrayList<>();
        QRestaurantImage restaurantImage = QRestaurantImage.restaurantImage;
        if (!imageRepository.exists(restaurantImage.restaurant.rstrId.eq(rstrId))) {

            String imageUrl = collect(rstrId, restaurant);
            if (StringUtils.hasText(imageUrl)) { // 이미지를 찾은 경우
                RestaurantImage image = RestaurantImage.builder()
                        .restaurant(restaurant)
                        .rstrImgUrl(imageUrl)
                        .build();
                imageRepository.saveAndFlush(image);
                images.add(image);
            }
        }
        return images;
    }
}