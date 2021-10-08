package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicValidationServiceTest {
    private BasicValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new BasicValidationService();
    }

    @Test()
    void validateAmount_admit_AmountIsMoreThen0() {
        assertThatCode(() ->
                validationService.validateAmount(46d)
        ).doesNotThrowAnyException();
    }

    @Test()
    void validateAmount_throwsException_AmountIsSmallerThen0() {
        assertThatThrownBy(() ->
                validationService.validateAmount(-500d)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Amount must be greater than 0");
    }

    @Test()
    void validateAmount_ThrowsException_AmountIsNull() {
        assertThatThrownBy(() ->
                validationService.validateAmount(null)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Amount must not be null");
    }

    @Test
    void validatePaymentId_throwsException_PaymentIdIsNull() {
        assertThatThrownBy(() ->
                validationService.validatePaymentId(null)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Payment id must not be null");
    }

    @Test
    void validatePaymentId_admit_PaymentIdIsCorrect() {
        assertThatCode(() ->
                validationService.validatePaymentId(UUID.randomUUID())
        ).doesNotThrowAnyException();
    }

    @Test
    void validateUserId_throwsException_UserIdIsNull() {
        assertThatThrownBy(() ->
                validationService.validateUserId(null)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("User id must not be null");
    }

    @Test
    void validateUserId_admit_UserIdIsCorrect() {
        assertThatCode(() ->
                validationService.validateUserId(Integer.MAX_VALUE)
        ).doesNotThrowAnyException();
    }

    @Test
    void validateUser_throwsException_UserIsInactive() {
        User inactiveUser = new User(Integer.MIN_VALUE, "Eni Eugen", Status.INACTIVE);
        assertThatThrownBy(() ->
                validationService.validateUser(inactiveUser)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with id " + inactiveUser.getId() + " not in ACTIVE status");
    }

    @Test
    void validateUser_admit_UserIsActive() {
        User activeUser = new User(Integer.MAX_VALUE, "Belov Oleg", Status.ACTIVE);

        assertThatCode(() ->
                validationService.validateUser(activeUser)
        ).doesNotThrowAnyException();
    }

    @Test
    void validateMessage_throwsException_MessageIsNull() {
        assertThatThrownBy(() ->
                validationService.validateMessage(null)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Payment message must not be null");
    }

    @Test
    void validateMessage_admit_MessageIsNotNull() {
        assertThatCode(() ->
                validationService.validateMessage("Not null string")
        ).doesNotThrowAnyException();
    }
}