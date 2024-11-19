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

package com.monkCommerce.CouponManagement.Controllers;

import com.monkCommerce.CouponManagement.DTO.CouponRequest;
import com.monkCommerce.CouponManagement.Entities.Coupon;
import com.monkCommerce.CouponManagement.Services.CouponServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponServices couponServices;

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody CouponRequest couponRequest) {
        return new ResponseEntity<>(couponServices.createCoupon(couponRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return new ResponseEntity<>(couponServices.getAllCoupons(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        return new ResponseEntity<>(couponServices.getCouponById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody CouponRequest couponRequest) {
        return new ResponseEntity<>(couponServices.updateCoupon(id, couponRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponServices.deleteCoupon(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
