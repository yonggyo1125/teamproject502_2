package com.jmt.restaurant.advices;

import com.jmt.wishlist.constants.WishType;
import com.jmt.wishlist.services.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice("com.jmt.restaurant")
@RequiredArgsConstructor
public class RestaurantControllerAdvice {
    private final WishListService wishListService;

    @ModelAttribute("restaurantWishList")
    public List<Long> RestaurantWishList() {
        return wishListService.getList(WishType.RESTAURANT);
    }
}
