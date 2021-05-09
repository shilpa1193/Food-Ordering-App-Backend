package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CommonValidation commonValidation;

    /**
     * RestController method called when the request pattern is of type "/order/coupon/{coupon_name}"
     * and the incoming request is of 'GET' type
     * Retrieve coupon details using coupon name
     *
     * @param authorization - String represents authorization token
     * @param couponName    - This represents coupon name
     * @return - ResponseEntity(CouponDetailsResponse, HttpStatus.OK)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out
     * @throws CouponNotFoundException      - if incorrect/ invalid coupon name is sent,
     *                                      or the coupon name doesn't exist
     */
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponDetails(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("coupon_name") final String couponName)
            throws AuthorizationFailedException, CouponNotFoundException {
        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        final CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);

        final CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse()
                .id(UUID.fromString(couponEntity.getUuid()))
                .couponName(couponEntity.getCouponName())
                .percent(couponEntity.getPercent());

        return new ResponseEntity<>(couponDetailsResponse, HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type "/order"
     * and the incoming request is of 'POST' type
     * Save order details
     *
     * @param authorization    - String represents authorization token
     * @param saveOrderRequest - SaveOrderRequest to be persisted into the database
     * @return - ResponseEntity(SaveOrderResponse, HttpStatus.CREATED)
     * @throws AuthorizationFailedException   - if incorrect/ invalid authorization Token is sent,
     *                                        or the user has already signed out
     * @throws CouponNotFoundException        - if invalid coupon uuid is sent
     * @throws AddressNotFoundException       - if invalid address uuid is sent, or the address does not belong to the logged in user
     * @throws PaymentMethodNotFoundException - if invalid payment uuid is sent
     * @throws RestaurantNotFoundException    - if invalid restaurant uuid is sent
     * @throws ItemNotFoundException          - if invalid item uuid is sent
     */
    @RequestMapping(method = RequestMethod.POST, path = "/order",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,
                                                       @RequestBody(required = false) final SaveOrderRequest saveOrderRequest)
            throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException,
            PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException {

        final String accessToken = authorization.split("Bearer ")[1];
        // Retrieve all the required entities to set in the OrderEntity
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        CouponEntity couponEntity = null;
        if (saveOrderRequest.getCouponId() != null) {
            couponEntity = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());
        }

        PaymentEntity paymentEntity = null;
        if (saveOrderRequest.getPaymentId() != null) {
            paymentEntity = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());
        }

        final AddressEntity addressEntity = addressService.getAddressByUUID(saveOrderRequest.getAddressId(), customerEntity);
        final RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString());

        // Set OrderItemEntity from the list of OrderItemEntity sent in request
        final List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        for (ItemQuantity itemQuantity : saveOrderRequest.getItemQuantities()) {
            final ItemEntity itemEntity = itemService.getItemById(itemQuantity.getItemId().toString());
            final OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setItem(itemEntity);
            orderItemEntity.setQuanity(itemQuantity.getQuantity());
            orderItemEntity.setPrice(itemQuantity.getPrice());
            orderItemEntities.add(orderItemEntity);
        }

        // Set OrderEntity
        final OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUuid(UUID.randomUUID().toString());
        orderEntity.setAddress(addressEntity);
        orderEntity.setPayment(paymentEntity);
        orderEntity.setBill(saveOrderRequest.getBill().doubleValue());
        orderEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
        orderEntity.setCoupon(couponEntity);
        orderEntity.setRestaurant(restaurantEntity);
        orderEntity.setCustomer(customerEntity);
        final Date currentDate = new Date();
        orderEntity.setDate(currentDate);

        final OrderEntity savedOrderEntity = orderService.saveOrder(orderEntity);

        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            orderItemEntity.setOrder(orderEntity);
            final OrderItemEntity savedOrderItemEntity = orderService.saveOrderItem(orderItemEntity);
        }

        final SaveOrderResponse saveOrderResponse = new SaveOrderResponse()
                .id(savedOrderEntity.getUuid())
                .status("ORDER SUCCESSFULLY PLACED");

        return new ResponseEntity<>(saveOrderResponse, HttpStatus.CREATED);
    }

    /**
     * RestController method called when the request pattern is of type "/order"
     * and the incoming request is of 'GET' type
     * Retrieve orders of a customer
     *
     * @param authorization - String represents authorization token
     * @return - ResponseEntity(CustomerOrderResponse, HttpStatus.OK)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out
     */
    @RequestMapping(method = RequestMethod.GET, path = "/order",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getAllPastOrders(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        final List<OrderEntity> orderEntities = orderService.getOrdersByCustomers(customerEntity.getUuid());

        final List<OrderList> orderLists = createOrderLists(orderEntities);

        final CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse()
                .orders(orderLists);
        return new ResponseEntity<>(customerOrderResponse, HttpStatus.OK);
    }

    /**
     * Method to set list of OrderEntity into OrderLists
     *
     * @param orderEntities -List of OrderEntity
     * @return - List of OrderList
     */
    private List<OrderList> createOrderLists(final List<OrderEntity> orderEntities) {
        final List<OrderList> orderLists = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntities) {
            final OrderList orderList = new OrderList();
            orderList.id(UUID.fromString(orderEntity.getUuid()));
            orderList.bill(BigDecimal.valueOf(orderEntity.getBill()));
            orderList.discount(BigDecimal.valueOf(orderEntity.getDiscount()));
            orderList.date(orderEntity.getDate().toString());

            // Set CouponEntity into OrderListCoupon
            final CouponEntity coupon = orderEntity.getCoupon();
            if (coupon != null) {
                final OrderListCoupon orderListCoupon = new OrderListCoupon();
                orderListCoupon.id(UUID.fromString(coupon.getUuid()));
                orderListCoupon.couponName(coupon.getCouponName());
                orderListCoupon.percent(coupon.getPercent());
                orderList.coupon(orderListCoupon);
            }

            // Set PaymentEntity into OrderListPayment
            final PaymentEntity payment = orderEntity.getPayment();
            if (payment != null) {
                final OrderListPayment orderListPayment = new OrderListPayment();
                orderListPayment.id(UUID.fromString(payment.getUuid()));
                orderListPayment.paymentName(payment.getPaymentName());
                orderList.payment(orderListPayment);
            }

            // Set CustomerEntity into OrderListCustomer
            final OrderListCustomer orderListCustomer = new OrderListCustomer();
            final CustomerEntity customer = orderEntity.getCustomer();
            orderListCustomer.id(UUID.fromString(customer.getUuid()));
            orderListCustomer.firstName(customer.getFirstName());
            orderListCustomer.lastName(customer.getLastName());
            orderListCustomer.emailAddress(customer.getEmail());
            orderListCustomer.contactNumber(customer.getContactNumber());
            orderList.customer(orderListCustomer);

            // Set AddressEntity into OrderListAddress
            final OrderListAddress orderListAddress = new OrderListAddress();
            final OrderListAddressState orderListAddressState = new OrderListAddressState();
            final AddressEntity address = orderEntity.getAddress();
            final StateEntity state = address.getState();
            orderListAddress.id(UUID.fromString(address.getUuid()));
            orderListAddress.flatBuildingName(address.getFlatBuilNo());
            orderListAddress.locality(address.getLocality());
            orderListAddress.city(address.getCity());
            orderListAddress.pincode(address.getPincode());
            orderListAddressState.id(UUID.fromString(state.getUuid()));
            orderListAddressState.stateName(state.getStateName());
            orderListAddress.state(orderListAddressState);
            orderList.address(orderListAddress);

            // Set List of OrderItemEntity into List of ItemQuantityResponse
            final List<ItemQuantityResponse> itemQuantityResponses = new ArrayList<>();
            final List<OrderItemEntity> orderItemEntities = orderService.getOrderItemsByOrderUuid(orderEntity.getUuid());
            for (OrderItemEntity orderItemEntity : orderItemEntities) {

                // Set ItemEntity into ItemQuantityResponseItem
                final ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();
                final ItemEntity item = orderItemEntity.getItem();
                itemQuantityResponseItem.id(UUID.fromString(item.getUuid()));
                itemQuantityResponseItem.itemName(item.getItemName());
                itemQuantityResponseItem.itemPrice(item.getPrice());
                final String itemType = item.getType().equals("0") ? "VEG" : "NON_VEG";
                itemQuantityResponseItem.type(ItemQuantityResponseItem.TypeEnum.fromValue(itemType));

                // Set OrderItemEntity into ItemQuantityResponse
                final ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();
                itemQuantityResponse.item(itemQuantityResponseItem);
                itemQuantityResponse.quantity(orderItemEntity.getQuanity());
                itemQuantityResponse.price(orderItemEntity.getPrice());

                itemQuantityResponses.add(itemQuantityResponse);
            }
            orderList.itemQuantities(itemQuantityResponses);
            orderLists.add(orderList);
        }
        return orderLists;
    }

}
