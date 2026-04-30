package com.logistics.load_optimizer.validation;

import com.logistics.load_optimizer.dto.OptimizeRequest;
import com.logistics.load_optimizer.model.Order;
import com.logistics.load_optimizer.model.Truck;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoDuplicateOrderIdsValidatorTest {

    private NoDuplicateOrderIdsValidator validator;
    
    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new NoDuplicateOrderIdsValidator();
    }

    @Test
    void testNoDuplicates() {
        Order order1 = new Order();
        order1.setId("ord-001");
        
        Order order2 = new Order();
        order2.setId("ord-002");

        OptimizeRequest request = new OptimizeRequest();
        request.setOrders(Arrays.asList(order1, order2));

        assertTrue(validator.isValid(request, context));
    }

    @Test
    void testWithDuplicates() {
        Order order1 = new Order();
        order1.setId("ord-001");
        
        Order order2 = new Order();
        order2.setId("ord-001");

        OptimizeRequest request = new OptimizeRequest();
        request.setOrders(Arrays.asList(order1, order2));

        assertFalse(validator.isValid(request, context));
    }

    @Test
    void testSingleOrder() {
        Order order = new Order();
        order.setId("ord-001");

        OptimizeRequest request = new OptimizeRequest();
        request.setOrders(Arrays.asList(order));

        assertTrue(validator.isValid(request, context));
    }

    @Test
    void testEmptyOrderList() {
        OptimizeRequest request = new OptimizeRequest();
        request.setOrders(List.of());

        assertTrue(validator.isValid(request, context));
    }

    @Test
    void testNullOrders() {
        OptimizeRequest request = new OptimizeRequest();
        request.setOrders(null);

        assertTrue(validator.isValid(request, context));
    }

    @Test
    void testMultipleDuplicates() {
        Order order1 = new Order();
        order1.setId("ord-001");
        
        Order order2 = new Order();
        order2.setId("ord-002");
        
        Order order3 = new Order();
        order3.setId("ord-001");

        OptimizeRequest request = new OptimizeRequest();
        request.setOrders(Arrays.asList(order1, order2, order3));

        assertFalse(validator.isValid(request, context));
    }

    @Test
    void testNullOrderId() {
        Order order1 = new Order();
        order1.setId(null);
        
        Order order2 = new Order();
        order2.setId("ord-002");

        OptimizeRequest request = new OptimizeRequest();
        request.setOrders(Arrays.asList(order1, order2));

        assertTrue(validator.isValid(request, context));
    }
}