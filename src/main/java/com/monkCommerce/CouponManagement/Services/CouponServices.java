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

import com.monkCommerce.CouponManagement.DTO.CouponRequest;
import com.monkCommerce.CouponManagement.Database.CouponDatabase;
import com.monkCommerce.CouponManagement.Entities.Coupon;
import com.monkCommerce.CouponManagement.Exceptions.CouponExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponServices {

    private static final Logger logger = LoggerFactory.getLogger(CouponServices.class);

    @Autowired
    private CouponDatabase couponDatabase;

    public Coupon createCoupon(CouponRequest couponRequest) {
        logger.debug("Creating coupon with details: {}", couponRequest);
        Coupon coupon = new Coupon();
        coupon.setType(couponRequest.getType());
        coupon.setDetails(couponRequest.getDetails());
        coupon.setExpirationDate(couponRequest.getExpirationDate());
        Coupon savedCoupon = couponDatabase.save(coupon);
        logger.info("Coupon created with ID: {}", savedCoupon.getId());
        return couponDatabase.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        logger.debug("Retrieving all coupons");
        return couponDatabase.findAll();
    }

    public Coupon getCouponById(Long id) {
        logger.debug("Retrieving coupon with ID: {}", id);
        return couponDatabase.findById(id).orElseThrow(() -> {
            logger.error("Coupon not found with ID: {}", id);
            return new CouponExceptions("Coupon not found");
        });
    }

    public Coupon updateCoupon(Long id, CouponRequest couponRequest) {
        logger.debug("Updating coupon with ID: {}", id);
        Coupon coupon = getCouponById(id);
        coupon.setType(couponRequest.getType());
        coupon.setDetails(couponRequest.getDetails());
        coupon.setExpirationDate(couponRequest.getExpirationDate());
        Coupon updatedCoupon = couponDatabase.save(coupon);
        logger.info("Coupon updated with ID: {}", updatedCoupon.getId());
        return couponDatabase.save(coupon);
    }

    public void deleteCoupon(Long id) {
        logger.debug("Deleting coupon with ID: {}", id);
        Coupon coupon = getCouponById(id);
        couponDatabase.delete(coupon);
        logger.info("Coupon deleted with ID: {}", id);
    }
}