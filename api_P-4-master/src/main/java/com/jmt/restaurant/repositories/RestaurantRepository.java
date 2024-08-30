package com.jmt.restaurant.repositories;

import com.jmt.restaurant.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, QuerydslPredicateExecutor<Restaurant> {

    // JPA
    @Query("SELECT r FROM Restaurant r left join r.foods m where concat(r.rstrLnnoAdres, r.rstrRdnmAdr) like :k1 and concat(m.menuNm, r.rstrIntrcnCont, r.rstrNm, r.reprsntMenuNm) like :k2")
    List<Restaurant> getRestaurants(@Param("k1") String key1, @Param("k2")String key2);

    @Query("SELECT count(r) FROM Restaurant r left join r.foods m where concat(r.rstrLnnoAdres, r.rstrRdnmAdr) like :k1 and concat(m.menuNm, r.rstrIntrcnCont, r.rstrNm, r.reprsntMenuNm) like :k2")
    Long getCountBy(@Param("k1") String key1, @Param("k2")String key2);

    /*
    //Querydsl로 한다면 ..
    default Long getCountBy1(String key1, String key2) {
        BooleanBuilder builder = new BooleanBuilder();
        QFoodMenu foodMenu = QFoodMenu.foodMenu;

        builder.and(foodMenu.restaurant.rstrLnnoAdres.concat(foodMenu.restaurant.rstrRdnmAdr).contains(key1));
        if (StringUtils.hasText(key2)) {
           builder.and(foodMenu.menuNm.concat(foodMenu.restaurant.rstrIntrcnCont).concat(foodMenu.restaurant.rstrNm).concat(foodMenu.restaurant.reprsntMenuNm).contains(key2));
        }
        return count(builder);
    }

    default  Long getCountBy1(String key1) {
        return getCountBy1(key1, null);
    }

    //
    default List<Restaurant> getRestaurants1(@Param("k1") String key1, @Param("k2")String key2) {

        BooleanBuilder builder = new BooleanBuilder();
        QFoodMenu foodMenu = QFoodMenu.foodMenu;
        builder.and(foodMenu.restaurant.rstrLnnoAdres.concat(foodMenu.restaurant.rstrRdnmAdr).contains(key1));
        if (StringUtils.hasText(key2)) {
            builder.and(foodMenu.menuNm.concat(foodMenu.restaurant.rstrIntrcnCont).concat(foodMenu.restaurant.rstrNm).concat(foodMenu.restaurant.reprsntMenuNm).contains(key2));
        }

        return (List<Restaurant>) findAll(builder);
    }
     */
 }
