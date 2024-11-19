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

package com.monkCommerce.CouponManagement;

import com.monkCommerce.CouponManagement.DTO.CouponRequest;
import com.monkCommerce.CouponManagement.Database.CouponDatabase;
import com.monkCommerce.CouponManagement.Entities.Coupon;
import com.monkCommerce.CouponManagement.Exceptions.CouponExceptions;
import com.monkCommerce.CouponManagement.Services.CouponServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class couponServicesTest {

    @Mock
    private CouponDatabase couponDatabase;

    @InjectMocks
    private CouponServices couponServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCoupon() {
        CouponRequest request = new CouponRequest();
        request.setType("cart-wise");
        request.setDetails("{\"threshold\": 100, \"discount\": 10}");
        request.setExpirationDate(LocalDateTime.of(2024, 12, 31, 23, 59, 59));

        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setType(request.getType());
        coupon.setDetails(request.getDetails());
        coupon.setExpirationDate(request.getExpirationDate());

        when(couponDatabase.save(any(Coupon.class))).thenReturn(coupon);

        Coupon createdCoupon = couponServices.createCoupon(request);

        assertNotNull(createdCoupon);
        assertEquals(1L, createdCoupon.getId());
        assertEquals("cart-wise", createdCoupon.getType());
        assertEquals("{\"threshold\": 100, \"discount\": 10}", createdCoupon.getDetails());
        assertEquals(LocalDateTime.of(2024, 12, 31, 23, 59, 59), createdCoupon.getExpirationDate());
    }

    @Test
    void testGetCouponById() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setType("cart-wise");
        coupon.setDetails("{\"threshold\": 100, \"discount\": 10}");
        coupon.setExpirationDate(LocalDateTime.of(2024, 12, 31, 23, 59, 59));

        when(couponDatabase.findById(1L)).thenReturn(Optional.of(coupon));

        Coupon foundCoupon = couponServices.getCouponById(1L);

        assertNotNull(foundCoupon);
        assertEquals(1L, foundCoupon.getId());
        assertEquals("cart-wise", foundCoupon.getType());
        assertEquals("{\"threshold\": 100, \"discount\": 10}", foundCoupon.getDetails());
        assertEquals(LocalDateTime.of(2024, 12, 31, 23, 59, 59), foundCoupon.getExpirationDate());
    }

    @Test
    void testGetCouponById_NotFound() {
        when(couponDatabase.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CouponExceptions.class, () -> couponServices.getCouponById(1L));
    }

    @Test
    void testUpdateCoupon() {
        CouponRequest request = new CouponRequest();
        request.setType("product-wise");
        request.setDetails("{\"product_id\": 1, \"discount\": 20}");
        request.setExpirationDate(LocalDateTime.of(2024, 12, 31, 23, 59, 59));

        Coupon existingCoupon = new Coupon();
        existingCoupon.setId(1L);
        existingCoupon.setType("cart-wise");
        existingCoupon.setDetails("{\"threshold\": 100, \"discount\": 10}");
        existingCoupon.setExpirationDate(LocalDateTime.of(2024, 12, 31, 23, 59, 59));

        when(couponDatabase.findById(1L)).thenReturn(Optional.of(existingCoupon));
        when(couponDatabase.save(any(Coupon.class))).thenReturn(existingCoupon);

        Coupon updatedCoupon = couponServices.updateCoupon(1L, request);

        assertNotNull(updatedCoupon);
        assertEquals(1L, updatedCoupon.getId());
        assertEquals("product-wise", updatedCoupon.getType());
        assertEquals("{\"product_id\": 1, \"discount\": 20}", updatedCoupon.getDetails());
        assertEquals(LocalDateTime.of(2024, 12, 31, 23, 59, 59), updatedCoupon.getExpirationDate());
    }

    @Test
    void testDeleteCoupon() {
        Coupon coupon = new Coupon();
        coupon.setId(1L);
        coupon.setType("cart-wise");
        coupon.setDetails("{\"threshold\": 100, \"discount\": 10}");
        coupon.setExpirationDate(LocalDateTime.of(2024, 12, 31, 23, 59, 59));

        when(couponDatabase.findById(1L)).thenReturn(Optional.of(coupon));
        doNothing().when(couponDatabase).delete(coupon);

        couponServices.deleteCoupon(1L);

        verify(couponDatabase, times(1)).delete(coupon);
    }
}
