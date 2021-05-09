package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    /**
     * RestController method called when the request pattern is of type "/payment"
     * and the incoming request is of 'GET' type
     * Retrieve all payment methods from the database
     *
     * @return ResponseEntity<PaymentListResponse> type object along with HttpStatus OK
     */
    @RequestMapping(value = "/payment", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getPaymentMethods() {
        final List<PaymentEntity> paymentEntityList = paymentService.getAllPaymentMethods();

        final List<PaymentResponse> paymentResponses = new ArrayList<>();
        for (PaymentEntity payment : paymentEntityList) {
            final PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.id(UUID.fromString(payment.getUuid()));
            paymentResponse.paymentName(payment.getPaymentName());
            paymentResponses.add(paymentResponse);
        }

        final PaymentListResponse paymentListResponse = new PaymentListResponse()
                .paymentMethods(paymentResponses);

        return new ResponseEntity<>(paymentListResponse, HttpStatus.OK);
    }
}
