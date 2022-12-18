package com.example.order.repository.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalTime;

/**
 * Holds meta data for CoffeeShop database entity
 *
 * @author Tharaka Weheragoda
 */

@Entity
@Table(name = "coffee_shop")
@Data
public class CoffeeShop {

    @Id
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "contact")
    private String contactNumber;
    @Column(name = "opening_time")
    private LocalTime openingTime;
    @Column(name = "closing_time")
    private LocalTime closingTime;
    @Column(name = "owner_id")
    private int ownerId;
    @Column(name = "queue_count")
    private int queueCount;
}
