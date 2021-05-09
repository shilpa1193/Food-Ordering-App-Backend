package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CommonValidation commonValidation;

    /**
     * Get  coupon details based on the provided coupon name
     *
     * @param couponName - String representing coupon name
     * @return - CouponEntity, if coupon exist
     * @throws CouponNotFoundException -if incorrect/ invalid coupon name is sent,
     *                                 or the coupon name doesn't exist
     */
    public CouponEntity getCouponByCouponName(final String couponName)
            throws CouponNotFoundException {

        // Throw exception if the coupon name is empty
        if (commonValidation.isEmptyFieldValue(couponName)) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }
        final CouponEntity couponEntity = orderDao.getCouponByCouponName(couponName);

        // Throw exception if the provided coupon name does not exist in the database
        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }
        return couponEntity;
    }

    /**
     * Method to get CouponEntity by coupon UUID
     *
     * @param couponUuid - coupon uuid
     * @return - CouponEntity
     * @throws CouponNotFoundException - if coupon uuid does not exist in the database
     */
    public CouponEntity getCouponByCouponId(final String couponUuid) throws CouponNotFoundException {
        final CouponEntity couponEntity = orderDao.getCouponByCouponUUID(couponUuid);

        // Throw exception if no CouponEntity is found in the database for the given coupon uuid
        if (couponEntity == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }
        return couponEntity;
    }

    /**
     * Method to save OrderEntity in the database
     *
     * @param orderEntity - OrderEntity to be persisted in the database
     * @return - persisted OrderEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(final OrderEntity orderEntity) {
        return orderDao.saveOrder(orderEntity);
    }

    /**
     * Method to save OrderItemEntity in the database
     *
     * @param orderItemEntity - OrderItemEntity to be persisted in the database
     * @return - persisted OrderItemEntity object
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(final OrderItemEntity orderItemEntity) {
        return orderDao.saveOrderItem(orderItemEntity);
    }

    /**
     * Method to get all orders for a customer
     *
     * @param customerUuid - Customer UUID
     * @return - List of OrderEntity objects
     */
    public List<OrderEntity> getOrdersByCustomers(final String customerUuid) {
        final List<OrderEntity> orderEntities = orderDao.getOrdersByCustomers(customerUuid);
        return orderEntities;
    }

    /**
     * Method to get all OrderItemEntity for the given oderUUID
     *
     * @param orderUuid - Order UUID
     * @return - List of OrderItemEntity obejcts
     */
    public List<OrderItemEntity> getOrderItemsByOrderUuid(final String orderUuid) {
        return orderDao.getOrderItemsByOrderUuid(orderUuid);
    }
}
