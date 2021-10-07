package com.endava.internship.mocking.repository;

import com.endava.internship.mocking.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class InMemPaymentRepositoryTest {

    private InMemPaymentRepository paymentRepository;
    private Payment payment1;
    private Payment payment2;
    private Payment payment3;

    @BeforeEach
    void setUp() {
        paymentRepository = new InMemPaymentRepository();
        payment1 = new Payment(1, 100d, "Payment from user 1");
        payment2 = new Payment(2, 300.56, "Payment from user 2");
        payment3 = new Payment(3, 0.1, "Payment from user 3");
    }

    @Test
    void save_admit_paymentIsCorrect() {
        Payment actualPayment = paymentRepository.save(payment1);

        assertAll(
                () -> assertThat(actualPayment).isNotNull(),
                () -> assertThat(actualPayment).isEqualTo(payment1),
                () -> assertThat(paymentRepository.findById(payment1.getPaymentId())).isPresent().get().isEqualTo(payment1)
        );

    }

    @Test
    void save_throwsException_PaymentRepeat() {
        paymentRepository.save(payment1);

        assertThatThrownBy(() ->
                paymentRepository.save(payment1)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment with id " + payment1.getPaymentId() + "already saved");
    }

    @Test
    void save_throwsException_PaymentIsIncorrect() {
        assertThatThrownBy(() ->
                paymentRepository.save(null)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Payment must not be null");
    }

    @Test
    void findById_Payment_IdIsCorrect() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        Optional<Payment> actualPayment = paymentRepository.findById(payment3.getPaymentId());

        assertAll(
                () -> assertThat(actualPayment).isPresent(),
                () -> assertThat(actualPayment.get()).isEqualTo(payment3)
        );
    }

    @Test
    void findById_Empty_IdIsNotPresent() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        Optional<Payment> actualPayment = paymentRepository.findById(UUID.randomUUID());

        assertThat(actualPayment.isPresent()).isFalse();
    }

    @Test
    void findById_throwsException_IdIsNull() {
        assertThatThrownBy(() ->
                paymentRepository.findById(null)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Payment id must not be null");
    }

    @Test
    void findAll_PaymentList() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);
        List<Payment> expectedPaymentList = asList(payment1, payment2, payment3);

        List<Payment> actualPaymentList = paymentRepository.findAll();

        assertAll(
                () -> assertThat(actualPaymentList).isNotNull(),
                () -> assertThat(actualPaymentList).isNotEmpty(),
                () -> assertThat(actualPaymentList).hasSize(3),
                () -> assertThat(actualPaymentList).containsAll(expectedPaymentList)
        );
    }

    @Test
    void editMessage_messageWasEdited_PaymentIdIsCorrect() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        String newMessage = "new payment message for payment 1";

        Payment actualPayment = paymentRepository.editMessage(payment1.getPaymentId(), newMessage);

        assertAll(
                () -> assertThat(actualPayment).isNotNull(),
                () -> assertThat(actualPayment).isEqualTo(payment1),
                () -> assertThat(actualPayment.getMessage()).isEqualTo(newMessage)
        );
    }

    @Test
    void editMessage_throwsException_IdIsIncorrect() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        String newMessage = "new payment message for payment 3";

        assertThatThrownBy(() ->
                paymentRepository.editMessage(payment3.getPaymentId(), newMessage)
        ).isInstanceOf(NoSuchElementException.class)
                .hasMessage("Payment with id " + payment3.getPaymentId() + " not found");
    }
}