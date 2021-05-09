package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get CustomerEntity based on the provided contact number
     *
     * @param contactNumber - String that represents contact number
     * @return - CustomerEntity object if customer exists for the provided contact number, else return null
     */
    public CustomerEntity getCustomerByContactNum(final String contactNumber) {
        try {
            return entityManager.createNamedQuery("customerByContactNum", CustomerEntity.class)
                    .setParameter("contactNum", contactNumber)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to persist CustomerEntity in the database
     *
     * @param customerEntity - CustomerEntity object that needs to persisted in the database
     * @return - persisted CustomerEntity
     */
    public CustomerEntity saveCustomer(final CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    /**
     * Method to persist CustomerAuthEntity in the database
     *
     * @param authEntity - CustomerAuthEntity to be persisted in the database
     */
    public void createCustomerAuth(final CustomerAuthEntity authEntity) {
        entityManager.persist(authEntity);
    }

    /**
     * Method to retrieve CustomerAuthEntity based on access token provided
     *
     * @param accessToken - String represents authorization/ access token
     * @return - CustomerAuthEntity if exists ib the database for given access token, else return null
     */
    public CustomerAuthEntity getCustomerAuth(final String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthByAccessToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to update CustomerAuthEntity object
     *
     * @param authEntity - CustomerAuthEntity object to be updated in the database
     */
    public void updateCustomerAuth(final CustomerAuthEntity authEntity) {
        entityManager.merge(authEntity);
    }

    /**
     * Method to update CustomerEntity on=bject
     *
     * @param customerEntity - CustomerEntity object to be updated in the database
     * @return - updated CustomerEntity
     */
    public CustomerEntity updateCustomer(final CustomerEntity customerEntity) {
        return entityManager.merge(customerEntity);
    }
}
