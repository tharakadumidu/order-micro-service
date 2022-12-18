package com.example.order.repository;

import com.example.order.repository.entity.CoffeeShopItemPricing;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Coffee shop menu item pricing repository
 *
 * @author Tharaka Weheragoda
 */
@Repository
public interface CoffeeShopItemPricingRepository extends CrudRepository<CoffeeShopItemPricing, Integer> {

}
