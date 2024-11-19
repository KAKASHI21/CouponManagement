# Coupons Management API

## Overview
This API manages and applies different types of discount coupons for an e-commerce platform.

## Implemented Cases
1. **Cart-wise Coupons**: Apply a discount to the entire cart if the total amount exceeds a certain threshold.
2. **Product-wise Coupons**: Apply a discount to specific products.
3. **BxGy Coupons**: "Buy X, Get Y" deals with a repetition limit.
4. **Implemented Unit Test**: Added unit Tests for relevant cases.
5. **Add Expiration Dates For Coupons**: Added Expiration dates for coupons and added checks to see if coupon is valid
6. **Implemented Logs**: For better monitoring implemented Logs

## Unimplemented Cases
1. **Separated Log files**: Separated Log files for error,Info,Request,SQL could not be implemented due to time constraints.

## Limitations
1. **Performance**: The current implementation may not be optimized for large datasets as it is in-memory.
2. **Scalability**: The system may need refactoring to handle more complex coupon types in the future.

## Assumptions
1. **Coupon Validity**: Coupons are assumed to be valid if not expired.
2. **Single Coupon Application**: Only one coupon can be applied to a cart at a time.

## Future Improvements
1. **Coupon Stacking**: Allow multiple coupons to be applied simultaneously.
2. **Enhanced Validation**: Add more robust validation for coupon conditions.
3. **Performance Optimization**: Optimize the system for better performance with large datasets.
4. **Implementation Of Custom Response Code**: Implement Custom response code to indicate success/failure and have different Response codes to indicate different type of errors.

## Running the Application
1. Run `mvn spring-boot:run`.

## API Endpoints
- `POST /coupons`: Create a new coupon.
- `GET /coupons`: Retrieve all coupons.
- `GET /coupons/{id}`: Retrieve a specific coupon by its ID.
- `PUT /coupons/{id}`: Update a specific coupon by its ID.
- `DELETE /coupons/{id}`: Delete a specific coupon by its ID.
- `POST /cart/applicable-coupons`: Fetch all applicable coupons for a given cart.
- `POST /cart/apply-coupon/{id}`: Apply a specific coupon to the cart.
