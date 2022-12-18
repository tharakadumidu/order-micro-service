package com.example.order.repository;

import com.example.order.repository.entity.CoffeeShop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Coffee shop repository
 *
 * @author Tharaka Weheragoda
 */
@Repository
public interface CoffeeShopRepository extends CrudRepository<CoffeeShop, Integer> {
}
