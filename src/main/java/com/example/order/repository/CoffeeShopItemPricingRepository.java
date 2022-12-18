package com.example.order.repository;

import com.example.order.repository.entity.CoffeeShopItemPricing;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeShopItemPricingRepository extends CrudRepository<CoffeeShopItemPricing, Integer> {

}
