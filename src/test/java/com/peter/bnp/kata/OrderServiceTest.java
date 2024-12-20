package com.peter.bnp.kata;

import com.peter.bnp.kata.exception.UserNotFoundException;
import com.peter.bnp.kata.model.Book;
import com.peter.bnp.kata.model.Order;
import com.peter.bnp.kata.model.OrderItem;
import com.peter.bnp.kata.model.User;
import com.peter.bnp.kata.repository.OrderRepository;
import com.peter.bnp.kata.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("peter");
    }

    @Test
    void getOrderForUserSuccess() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalPrice(100.0);

        OrderItem orderItem = new OrderItem();
        orderItem.setBook(new Book(1L, "Java Programming", "James Gosling", 10.0));
        orderItem.setQuantity(2);
        orderItem.setTotalPrice(20.0);

        order.setOrderItems(List.of(orderItem));

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(Optional.of(order));

        List<Order> orders = orderService.getOrdersForUser(user.getUsername());

        assertThat(orders.size()).isEqualTo(1);
        assertThat(orders.get(0).getTotalPrice()).isEqualTo(20.0);
        assertThat(orders.get(0).getOrderItems().size()).isEqualTo(1);
        assertThat(orders.get(0).getOrderItems().get(0).getBook().getTitle()).isEqualTo("Java Programming");

        verify(userRepository).findByUsername(user.getUsername());
        verify(orderRepository).findByUser(user);
    }

    @Test
    void getOrderForUserNoUserFound() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderService.getOrdersForUser(user.getUsername()));

        verify(userRepository).findByUsername(user.getUsername());
        verifyNoInteractions(orderRepository);
    }
}
