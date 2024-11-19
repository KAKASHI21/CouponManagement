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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monkCommerce.CouponManagement.Controllers.CartController;
import com.monkCommerce.CouponManagement.DTO.CartRequest;
import com.monkCommerce.CouponManagement.Entities.Cart;
import com.monkCommerce.CouponManagement.Entities.CartItem;
import com.monkCommerce.CouponManagement.Entities.Coupon;
import com.monkCommerce.CouponManagement.Services.CartServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartServices cartServices;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    void testGetApplicableCoupons() throws Exception {
        CartRequest cartRequest = new CartRequest();
        cartRequest.setItems(Arrays.asList(
                new CartItem(1L, 6, 50.0, 0.0),
                new CartItem(2L, 3, 30.0, 0.0),
                new CartItem(3L, 2, 25.0, 0.0)
        ));

        Coupon cartWiseCoupon = new Coupon();
        cartWiseCoupon.setId(1L);
        cartWiseCoupon.setType("cart-wise");
        cartWiseCoupon.setDetails("{\"threshold\": 100, \"discount\": 10}");
        cartWiseCoupon.setExpirationDate(LocalDateTime.of(2024, 12, 31, 23, 59, 59));

        Coupon productWiseCoupon = new Coupon();
        productWiseCoupon.setId(2L);
        productWiseCoupon.setType("product-wise");
        productWiseCoupon.setDetails("{\"product_id\": 1, \"discount\": 20}");
        productWiseCoupon.setExpirationDate(LocalDateTime.of(2024, 12, 31, 23, 59, 59));

        List<Coupon> applicableCoupons = Arrays.asList(cartWiseCoupon, productWiseCoupon);

        when(cartServices.getApplicableCoupons(any(CartRequest.class))).thenReturn(applicableCoupons);

        mockMvc.perform(post("/cart/applicable-coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("cart-wise"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].type").value("product-wise"));

        verify(cartServices, times(1)).getApplicableCoupons(any(CartRequest.class));
    }

    @Test
    void testApplyCoupon() throws Exception {
        CartRequest cartRequest = new CartRequest();
        cartRequest.setItems(Arrays.asList(
                new CartItem(1L, 6, 50.0, 0.0),
                new CartItem(2L, 3, 30.0, 0.0),
                new CartItem(3L, 2, 25.0, 0.0)
        ));

        Cart cart = new Cart();
        cart.setItems(cartRequest.getItems());
        cart.setTotalPrice(490.0);
        cart.setTotalDiscount(50.0);
        cart.setFinalPrice(440.0);

        when(cartServices.applyCoupon(eq(1L), any(CartRequest.class))).thenReturn(cart);

        mockMvc.perform(post("/cart/apply-coupon/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(490.0))
                .andExpect(jsonPath("$.totalDiscount").value(50.0))
                .andExpect(jsonPath("$.finalPrice").value(440.0));

        verify(cartServices, times(1)).applyCoupon(eq(1L), any(CartRequest.class));
    }
}
