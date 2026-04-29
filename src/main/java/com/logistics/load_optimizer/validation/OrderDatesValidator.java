package com.logistics.load_optimizer.validation;

import com.logistics.load_optimizer.model.Order;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class OrderDatesValidator implements ConstraintValidator<ValidOrderDates, Order> {

    @Override
    public boolean isValid(Order order, ConstraintValidatorContext context) {
        if (order.getPickupDate() == null || order.getDeliveryDate() == null) {
            return true;
        }
        try {
            LocalDate pickup = LocalDate.parse(order.getPickupDate());
            LocalDate delivery = LocalDate.parse(order.getDeliveryDate());
            return !delivery.isBefore(pickup);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}