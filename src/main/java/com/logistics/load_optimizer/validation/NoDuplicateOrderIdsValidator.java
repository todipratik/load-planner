package com.logistics.load_optimizer.validation;

import com.logistics.load_optimizer.dto.OptimizeRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class NoDuplicateOrderIdsValidator implements ConstraintValidator<NoDuplicateOrderIds, OptimizeRequest> {

    @Override
    public boolean isValid(OptimizeRequest request, ConstraintValidatorContext context) {
        if (request.getOrders() == null) {
            return true; // let @NotNull handle this
        }
        Set<String> seen = new HashSet<>();
        for (var order : request.getOrders()) {
            if (order.getId() == null) continue; // let @NotBlank handle this
            if (!seen.add(order.getId())) {
                return false;
            }
        }
        return true;
    }
}