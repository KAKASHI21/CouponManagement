package com.monkCommerce.CouponManagement.Controllers;

import com.monkCommerce.CouponManagement.DTO.CartRequest;
import com.monkCommerce.CouponManagement.Entities.Cart;
import com.monkCommerce.CouponManagement.Entities.Coupon;
import com.monkCommerce.CouponManagement.Services.CartServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartServices cartServices;

    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<Coupon>> getApplicableCoupons(@RequestBody CartRequest cartRequest) {
        logger.debug("Received request to fetch applicable coupons for cart: {}", cartRequest);
        List<Coupon> coupons = cartServices.getApplicableCoupons(cartRequest);
        logger.info("Retrieved {} applicable coupons for cart", coupons.size());
        return new ResponseEntity<>(coupons, HttpStatus.OK);
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<Cart> applyCoupon(@PathVariable Long id, @RequestBody CartRequest cartRequest) {
        logger.debug("Received request to apply coupon with ID: {} to cart: {}", id, cartRequest);
        Cart cart = cartServices.applyCoupon(id, cartRequest);
        logger.info("Applied coupon with ID: {} to cart", id);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }
}
