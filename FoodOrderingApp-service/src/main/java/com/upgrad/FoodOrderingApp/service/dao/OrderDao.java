package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get  coupon details based on the provided coupon name
     *
     * @param couponName - String that represents coupon name
     * @return - CouponEntity object if coupon exists for the provided coupon name, else return null
     */
    public CouponEntity getCouponByCouponName(final String couponName) {
        try {
            return entityManager.createNamedQuery("couponByCouponName", CouponEntity.class)
                    .setParameter("couponName", couponName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to get CouponEntity for the given coupon UUID
     *
     * @param couponUuid - STring representing coupon UUID
     * @return CouponEntity if present in the database, else return null
     */
    public CouponEntity getCouponByCouponUUID(final String couponUuid) {
        try {
            return entityManager.createNamedQuery("couponByUuid", CouponEntity.class)
                    .setParameter("uuid", couponUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to persist order in the database
     *
     * @param orderEntity - OrderEntity object to be persisted in the database
     * @return - persisted OrderEntity object
     */
    public OrderEntity saveOrder(final OrderEntity orderEntity) {
        entityManager.persist(orderEntity);
        return orderEntity;
    }

    /**
     * Method to persist OrderItemEntity in the database
     *
     * @param orderItemEntity - OrderItemEntity to be persisted in the database
     * @return - persisted OrderItemEntity object
     */
    public OrderItemEntity saveOrderItem(final OrderItemEntity orderItemEntity) {
        entityManager.persist(orderItemEntity);
        return orderItemEntity;
    }

    /**
     * Method to get all OrderEntity for the customer
     *
     * @param customerUuid - Customer UUID
     * @return - List of OrderEntity objects
     */
    public List<OrderEntity> getOrdersByCustomers(final String customerUuid) {
        return entityManager.createNamedQuery("orderByCustomers", OrderEntity.class)
                .setParameter("customerUuid", customerUuid)
                .getResultList();
    }

    /**
     * Method to get all OrderItemEntity for an order
     *
     * @param orderUuid - Order UUID
     * @return - List of OrderItemEntity objects
     */
    public List<OrderItemEntity> getOrderItemsByOrderUuid(final String orderUuid) {
        return entityManager.createNamedQuery("orderItemsByOrder", OrderItemEntity.class)
                .setParameter("orderUuid", orderUuid)
                .getResultList();
    }

    /**
     * Method to get list of OrderEntity on a particular address
     * @param addressEntity - AddressEntity object
     * @return - List of OrderEntity
     */
    public List<OrderEntity> getOrdersByAddress(AddressEntity addressEntity) {
        return entityManager.createNamedQuery("ordersByAddress", OrderEntity.class)
                .setParameter("addressUuid", addressEntity.getUuid())
                .getResultList();
    }
}
