/*
 * Copyright (C) 2024 Shaik Iftekhar Ahmed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.monkCommerce.CouponManagement.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkCommerce.CouponManagement.DTO.CartRequest;
import com.monkCommerce.CouponManagement.Database.CouponDatabase;
import com.monkCommerce.CouponManagement.Entities.Cart;
import com.monkCommerce.CouponManagement.Entities.CartItem;
import com.monkCommerce.CouponManagement.Entities.Coupon;
import com.monkCommerce.CouponManagement.Exceptions.CouponExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartServices {

    private static final Logger logger = LoggerFactory.getLogger(CartServices.class);

    @Autowired
    private CouponDatabase couponDatabase;

    public List<Coupon> getApplicableCoupons(CartRequest cartRequest) {
        logger.debug("Fetching applicable coupons for cart: {}", cartRequest);
        Cart cart = new Cart();
        cart.setItems(cartRequest.getItems());
        calculateTotalPrice(cart);
        return couponDatabase.findAll().stream()
                .filter(coupon -> isCouponApplicable(coupon, cart))
                .collect(Collectors.toList());
    }

    public Cart applyCoupon(Long id, CartRequest cartRequest) {
        logger.debug("Applying coupon with ID: {} to cart: {}", id, cartRequest);
        Coupon coupon = couponDatabase.findById(id).orElseThrow(() -> {
            logger.error("Coupon not found with ID: {}", id);
            return new CouponExceptions("Coupon not found");
        });

        if (coupon.getExpirationDate().isBefore(LocalDateTime.now())) { // Bonus: Add expiration dates for coupons.
            logger.error("Coupon with ID: {} is expired", id);
            throw new CouponExceptions("Coupon is expired");
        }

        Cart cart = new Cart();
        cart.setItems(cartRequest.getItems());
        calculateTotalPrice(cart);
        applyCouponToCart(coupon, cart);
        logger.info("Coupon applied with ID: {} to cart", id);
        return cart;
    }

    private boolean isCouponApplicable(Coupon coupon, Cart cart) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> details = mapper.readValue(coupon.getDetails(), Map.class);

            switch (coupon.getType()) {
                case "cart-wise":
                    double threshold = ((Number) details.get("threshold")).doubleValue();
                    return cart.getTotalPrice() > threshold;
                case "product-wise":
                    Long productId = ((Number) details.get("product_id")).longValue();
                    return cart.getItems().stream().anyMatch(item -> item.getProductId().equals(productId));
                case "bxgy":
                    List<Map<String, Object>> buyProducts = (List<Map<String, Object>>) details.get("buy_products");
                    int buyCount = 0;
                    for (Map<String, Object> buyProduct : buyProducts) {
                        Long buyProductId = ((Number) buyProduct.get("product_id")).longValue();
                        int quantity = ((Number) buyProduct.get("quantity")).intValue();
                        for (CartItem item : cart.getItems()) {
                            if (item.getProductId().equals(buyProductId)) {
                                buyCount += item.getQuantity() / quantity;
                            }
                        }
                    }
                    return buyCount > 0;
                default:
                    return false;
            }
        } catch (IOException e) {
            logger.error("Error parsing coupon details: {}", e.getMessage());
            return false;
        }
    }

    private void calculateTotalPrice(Cart cart) {
        double totalPrice = 0.0;
        for (CartItem item : cart.getItems()) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        cart.setTotalPrice(totalPrice);
    }

    private void applyCouponToCart(Coupon coupon, Cart cart) {
        switch (coupon.getType()) {
            case "cart-wise":
                applyCartWiseCoupon(coupon, cart);
                break;
            case "product-wise":
                applyProductWiseCoupon(coupon, cart);
                break;
            case "bxgy":
                applyBxGyCoupon(coupon, cart);
                break;
            default:
                logger.warn("Unknown coupon type: {}", coupon.getType());
        }
    }

    private void applyCartWiseCoupon(Coupon coupon, Cart cart) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> details = mapper.readValue(coupon.getDetails(), Map.class);
            double threshold = ((Number) details.get("threshold")).doubleValue();
            double discount = ((Number) details.get("discount")).doubleValue();

            if (cart.getTotalPrice() > threshold) {
                double discountAmount = cart.getTotalPrice() * (discount / 100);
                cart.setTotalDiscount(discountAmount);
                cart.setFinalPrice(cart.getTotalPrice() - discountAmount);
            }
        } catch (IOException e) {
            logger.error("Error parsing coupon details: {}", e.getMessage());
        }
    }

    private void applyProductWiseCoupon(Coupon coupon, Cart cart) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> details = mapper.readValue(coupon.getDetails(), Map.class);
            Long productId = ((Number) details.get("product_id")).longValue();
            double discount = ((Number) details.get("discount")).doubleValue();

            for (CartItem item : cart.getItems()) {
                if (item.getProductId().equals(productId)) {
                    double discountAmount = item.getPrice() * (discount / 100) * item.getQuantity();
                    item.setTotalDiscount(discountAmount);
                    cart.setTotalDiscount(cart.getTotalDiscount() + discountAmount);
                }
            }
            cart.setFinalPrice(cart.getTotalPrice() - cart.getTotalDiscount());
        } catch (IOException e) {
            logger.error("Error parsing coupon details: {}", e.getMessage());
        }
    }

    private void applyBxGyCoupon(Coupon coupon, Cart cart) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> details = mapper.readValue(coupon.getDetails(), Map.class);
            List<Map<String, Object>> buyProducts = (List<Map<String, Object>>) details.get("buy_products");
            List<Map<String, Object>> getProducts = (List<Map<String, Object>>) details.get("get_products");
            int repetitionLimit = ((Number) details.get("repetition_limit")).intValue();

            int buyCount = 0;
            for (Map<String, Object> buyProduct : buyProducts) {
                Long productId = ((Number) buyProduct.get("product_id")).longValue();
                int quantity = ((Number) buyProduct.get("quantity")).intValue();

                for (CartItem item : cart.getItems()) {
                    if (item.getProductId().equals(productId)) {
                        buyCount += item.getQuantity() / quantity;
                    }
                }
            }

            int applicableRepetitions = Math.min(buyCount, repetitionLimit);

            for (Map<String, Object> getProduct : getProducts) {
                Long productId = ((Number) getProduct.get("product_id")).longValue();
                int quantity = ((Number) getProduct.get("quantity")).intValue();

                for (CartItem item : cart.getItems()) {
                    if (item.getProductId().equals(productId)) {
                        double discountAmount = item.getPrice() * quantity * applicableRepetitions;
                        item.setTotalDiscount(discountAmount);
                        cart.setTotalDiscount(cart.getTotalDiscount() + discountAmount);
                    }
                }
            }
            cart.setFinalPrice(cart.getTotalPrice() - cart.getTotalDiscount());
        } catch (IOException e) {
            logger.error("Error parsing coupon details: {}", e.getMessage());
        }
    }
}
