package com.lht.lhtsharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leo
 * @date 2024/11/04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private int id;
    private int uid;
    private double price;

}
