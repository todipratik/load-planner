package com.logistics.load_optimizer.validation;

import com.logistics.load_optimizer.model.Order;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class OrderDatesValidatorTest {

    private OrderDatesValidator validator;
    
    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new OrderDatesValidator();
    }

    @Test
    void testValidDates_DeliveryAfterPickup() {
        Order order = new Order();
        order.setPickupDate("2025-12-05");
        order.setDeliveryDate("2025-12-09");

        assertTrue(validator.isValid(order, context));
    }

    @Test
    void testValidDates_SameDayPickupAndDelivery() {
        Order order = new Order();
        order.setPickupDate("2025-12-05");
        order.setDeliveryDate("2025-12-05");

        assertTrue(validator.isValid(order, context));
    }

    @Test
    void testInvalidDates_DeliveryBeforePickup() {
        Order order = new Order();
        order.setPickupDate("2025-12-09");
        order.setDeliveryDate("2025-12-05");

        assertFalse(validator.isValid(order, context));
    }

    @Test
    void testInvalidDateFormat_PickupDate() {
        Order order = new Order();
        order.setPickupDate("invalid-date");
        order.setDeliveryDate("2025-12-09");

        assertFalse(validator.isValid(order, context));
    }

    @Test
    void testInvalidDateFormat_DeliveryDate() {
        Order order = new Order();
        order.setPickupDate("2025-12-05");
        order.setDeliveryDate("09-12-2025");

        assertFalse(validator.isValid(order, context));
    }

    @Test
    void testNullDates() {
        Order order = new Order();
        order.setPickupDate(null);
        order.setDeliveryDate(null);

        assertTrue(validator.isValid(order, context));
    }

    @Test
    void testNullPickupDate() {
        Order order = new Order();
        order.setPickupDate(null);
        order.setDeliveryDate("2025-12-09");

        assertTrue(validator.isValid(order, context));
    }

    @Test
    void testNullDeliveryDate() {
        Order order = new Order();
        order.setPickupDate("2025-12-05");
        order.setDeliveryDate(null);

        assertTrue(validator.isValid(order, context));
    }
}