package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Payment;
import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import com.endava.internship.mocking.repository.PaymentRepository;
import com.endava.internship.mocking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ValidationService validationService;
    @InjectMocks
    private PaymentService paymentService;
    private final ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

    @BeforeEach
    void setUp() {
    }

    @Test()
    void createPayment() {
        final int id = 1;
        final double amount = 0.1;
        final String name = "John Doe";
        final Status status = Status.ACTIVE;
        final String message = "Payment from user " + name;
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(new User(id, name, status)));
        Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(Mockito.mock(Payment.class));

        Payment payment = paymentService.createPayment(id, amount);

        assertAll(
                () -> verify(validationService).validateUserId(id),
                () -> verify(validationService).validateAmount(amount),
                () -> verify(validationService).validateUser(new User(id, name, status)),
                () -> verify(userRepository).findById(id),
                () -> verify(paymentRepository).save(paymentCaptor.capture()),
                () -> assertThat(paymentCaptor.getValue()).isNotNull(),
                () -> assertThat(paymentCaptor.getValue().getPaymentId()).isNotNull(),
                () -> assertThat(paymentCaptor.getValue().getAmount()).isEqualTo(amount),
                () -> assertThat(paymentCaptor.getValue().getMessage()).isEqualTo(message),
                () -> assertThat(payment).isNotNull(),
                () -> assertThat(payment).isInstanceOf(Payment.class)
        );

    }

    @Test
    void editMessage() {
        final String message = "Payment from user Abraham";
        final UUID uuid = randomUUID();
        Mockito.when(paymentRepository.editMessage(uuid, message)).thenReturn(mock(Payment.class));

        Payment payment = paymentService.editPaymentMessage(uuid, message);

        assertAll(
                () -> verify(validationService).validatePaymentId(uuid),
                () -> verify(validationService).validateMessage(message),
                () -> verify(paymentRepository).editMessage(uuid, message),
                () -> assertThat(payment).isNotNull(),
                () -> assertThat(payment).isInstanceOf(Payment.class)
        );

    }

    @Test
    void getAllByAmountExceeding() {
        List<Payment> paymentList = asList(
                new Payment(1, 100d, "Payment from user A"),
                new Payment(2, 200d, "Payment from user B"),
                new Payment(3, 500d, "Payment from user C"),
                new Payment(4, 500.01, "Payment from user D"),
                new Payment(5, 10000d, "Payment from user E"),
                new Payment(4, 500.001, "Payment from user F"),
                new Payment(5, 500.00001, "Payment from user G")
        );
        final double amount = 500.00001;
        Mockito.when(paymentRepository.findAll()).thenReturn(paymentList);
        List<Payment> expectedList = paymentList.stream()
                .filter(payment -> payment.getAmount() > amount)
                .collect(Collectors.toList());

        List<Payment> actualList = paymentService.getAllByAmountExceeding(amount);

        assertAll(
                () -> Mockito.verify(paymentRepository).findAll(),
                () -> assertThat(actualList).containsAll(expectedList),
                () -> assertThat(actualList).doesNotContain(new Payment(1, 100d, "Payment from user A"), new Payment(5, 500.00001, "Payment from user G")),
                () -> assertThat(actualList).hasSize(expectedList.size())
        );
    }
}
