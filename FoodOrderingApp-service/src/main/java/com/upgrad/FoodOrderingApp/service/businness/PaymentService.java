package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;

import java.util.List;

import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    /**
     * Method to retrieve all the payment methods from the database
     *
     * @return List<PaymentEntity> object
     */

    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();
    }

    /**
     * Method to retrieve PaymentEntity for the given payment UUID
     *
     * @param paymentUuid - Payment UUID
     * @return - PaymentEntity object
     * @throws PaymentMethodNotFoundException - if no PaymentEntity in found in the database for the given payment UUID
     */
    public PaymentEntity getPaymentByUUID(final String paymentUuid) throws PaymentMethodNotFoundException {
        final PaymentEntity paymentEntity = paymentDao.getPaymentByUUID(paymentUuid);
        if (paymentEntity == null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
        return paymentEntity;
    }
}
