package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to retrieve all payment methods available in the database
     *
     * @return - List of PaymentEntity object
     */
    public List<PaymentEntity> getAllPaymentMethods() {
        return entityManager.createNamedQuery("allPaymentMethods", PaymentEntity.class)
                .getResultList();
    }

    /**
     * Method to get PaymentEntity for the given payment uuid
     *
     * @param paymentUuid - String represents payment UUID
     * @return PaymentEntity if found in the database, else return null
     */
    public PaymentEntity getPaymentByUUID(final String paymentUuid) {
        try {
            return this.entityManager.createNamedQuery("paymentByUUID", PaymentEntity.class)
                    .setParameter("paymentUUID", paymentUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}