package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@RestController
@CrossOrigin
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CustomerService customerService;

    /**
     * RestController method called when the request pattern is of type /restaurant"
     * and the incoming request is of 'GET' type
     * fetch all restaurants
     *
     * @return - ResponseEntity(RestaurantListResponse, HttpStatus.OK)
     */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {

        final List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByRating();

        return new ResponseEntity<>(createRestaurantListResponse(restaurantEntities), HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type /restaurant/name/{restaurant_name}"
     * and the incoming request is of 'GET' type
     * fetch all restaurants matched with restaurant_name
     *
     * @param restaurantName - SString representing restaurant name
     * @return - ResponseEntity(RestaurantListResponse, HttpStatus.OK)
     * @throws RestaurantNotFoundException - if the restaurant name is empty
     */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> restaurantsByName(
            @PathVariable("restaurant_name") final String restaurantName)
            throws RestaurantNotFoundException {
        final List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByName(restaurantName);

        return new ResponseEntity<>(createRestaurantListResponse(restaurantEntities), HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type /restaurant/category/{category_id}
     * and the incoming request is of 'GET' type
     * fetch all restaurants using category id
     *
     * @param categoryId - String represents category id
     * @return - ResponseEntity(RestaurantListResponse, HttpStatus.OK)
     * @throws CategoryNotFoundException - if empty or invalid category is passed
     */

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> restaurantByCategoryId(
            @PathVariable("category_id") final String categoryId)
            throws CategoryNotFoundException {
        final List<RestaurantEntity> restaurantEntities = restaurantService.restaurantByCategory(categoryId);

        return new ResponseEntity<>(createRestaurantListResponse(restaurantEntities), HttpStatus.OK);
    }


    /**
     * RestController method called when the request pattern is of type /restaurant/{restaurant_id}
     * and the incoming request is of 'GET' type
     * fetch all restaurants using restaurant id
     *
     * @param restaurantId - String represents restaurant id
     * @return - ResponseEntity(RestaurantDetailsResponse, HttpStatus.OK)
     * @throws RestaurantNotFoundException - if empty or invalid restaurant id is passed
     */

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> restaurantByRestaurantId(
            @PathVariable("restaurant_id") final String restaurantId)
            throws RestaurantNotFoundException {
        final RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);

        final RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();

        restaurantDetailsResponse.id(UUID.fromString(restaurantEntity.getUuid()));
        restaurantDetailsResponse.restaurantName(restaurantEntity.getRestaurantName());
        restaurantDetailsResponse.photoURL(restaurantEntity.getPhotoUrl());
        restaurantDetailsResponse.customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
        restaurantDetailsResponse.averagePrice(restaurantEntity.getAvgPrice());
        restaurantDetailsResponse.numberCustomersRated(restaurantEntity.getNumberCustomersRated());

        //set address of type RestaurantDetailsResponseAddress to restaurantList
        restaurantDetailsResponse.address(createRestaurantDetailsResponseAddress(restaurantEntity.getAddress()));

        final List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantId);
        if (!categoryEntities.isEmpty()) {
            final List<CategoryList> categoryLists = new ArrayList<>();
            categoryEntities.forEach(categoryEntity -> categoryLists.add(createCategoryList(restaurantId, categoryEntity)));
            restaurantDetailsResponse.categories(categoryLists);
        }

        return new ResponseEntity<>(restaurantDetailsResponse, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.PUT, path = ("/restaurant/{restaurant_id}"),
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("restaurant_id") final String restaurantId,
            @RequestParam("customer_rating") final Double customerRating)
            throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {

        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);
        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantRating(restaurantEntity, customerRating);
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse();
        restaurantUpdatedResponse.id(UUID.fromString(restaurantId));
        restaurantUpdatedResponse.status("RESTAURANT RATING UPDATED SUCCESSFULLY");

        return new ResponseEntity<>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    /**
     * Method to set RestaurantListResponse from List of RestaurantEntity
     *
     * @param restaurantEntities - List of RestaurantEntity
     * @return - RestaurantListResponse object
     */
    private RestaurantListResponse createRestaurantListResponse(final List<RestaurantEntity> restaurantEntities) {
        final RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        if (!restaurantEntities.isEmpty()) {
            final List<RestaurantList> restaurantLists = new ArrayList<>();
            for (RestaurantEntity restaurantEntity : restaurantEntities) {
                final RestaurantList restaurantList = createRestaurantList(restaurantEntity);
                restaurantList.address(createRestaurantDetailsResponseAddress(restaurantEntity.getAddress()));
                final List<CategoryEntity> categoryEntities = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
                restaurantList.categories(createCategoryName(categoryEntities));
                restaurantLists.add(restaurantList);
            }
            restaurantListResponse.restaurants(restaurantLists);
        }

        return restaurantListResponse;
    }

    /**
     * Method to set values into RestaurantList from RestaurantEntity
     *
     * @param restaurantEntity - RestaurantEntity object
     * @return - RestaurantList object
     */
    private RestaurantList createRestaurantList(final RestaurantEntity restaurantEntity) {
        final RestaurantList restaurantList = new RestaurantList();
        restaurantList.id(UUID.fromString(restaurantEntity.getUuid()));
        restaurantList.restaurantName(restaurantEntity.getRestaurantName());
        restaurantList.photoURL(restaurantEntity.getPhotoUrl());
        restaurantList.customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
        restaurantList.averagePrice(restaurantEntity.getAvgPrice());
        restaurantList.numberCustomersRated(restaurantEntity.getNumberCustomersRated());
        return restaurantList;
    }

    /**
     * Method to set values into RestaurantDetailsResponseAddress from AddressEntity
     *
     * @param addressEntity - AddressEntity object
     * @return - RestaurantDetailsResponseAddress object
     */
    private RestaurantDetailsResponseAddress createRestaurantDetailsResponseAddress(final AddressEntity addressEntity) {
        final RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();
        responseAddress.id(UUID.fromString(addressEntity.getUuid()));
        responseAddress.flatBuildingName(addressEntity.getFlatBuilNo());
        responseAddress.locality(addressEntity.getLocality());
        responseAddress.city(addressEntity.getCity());
        responseAddress.pincode(addressEntity.getPincode());

        //Setting state of that address of type RestaurantDetailsResponseAddressState
        final RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();
        final StateEntity stateEntity = addressEntity.getState();
        responseAddressState.id(UUID.fromString(stateEntity.getUuid()));
        responseAddressState.stateName(stateEntity.getStateName());

        //setting state of type RestaurantDetailsResponseAddressState in address of type RestaurantDetailsResponseAddress
        responseAddress.state(responseAddressState);
        return responseAddress;
    }

    /**
     * Method to set all Category name into a String
     *
     * @param categoryEntities - List of CategoryEntity object
     * @return - String representing all category name
     */
    private String createCategoryName(final List<CategoryEntity> categoryEntities) {
        StringJoiner categoryName = new StringJoiner(", ");
        for (CategoryEntity categoryEntity : categoryEntities) {
            categoryName.add(categoryEntity.getCategoryName());
        }
        return categoryName.toString();
    }

    /**
     * Method to set values into CategoryList for CategoryEntity
     *
     * @param restaurantUuid - Restaurant Uuid
     * @param categoryEntity - CategoryEntity obejct
     * @return - CategoryList
     */
    private CategoryList createCategoryList(final String restaurantUuid, final CategoryEntity categoryEntity) {
        final CategoryList categoryList = new CategoryList();
        categoryList.id(UUID.fromString(categoryEntity.getUuid()));
        categoryList.categoryName(categoryEntity.getCategoryName());

        final List<ItemEntity> itemEntities =
                itemService.getItemsByCategoryAndRestaurant(restaurantUuid, categoryEntity.getUuid());
        final List<ItemList> itemLists = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntities) {
            itemLists.add(createItemList(itemEntity));
        }
        categoryList.itemList(itemLists);
        return categoryList;
    }

    /**
     * Method to set values into ItemList from ItemEntity
     *
     * @param itemEntity - ItemEntity object
     * @return - ItemList object
     */
    private ItemList createItemList(final ItemEntity itemEntity) {
        final ItemList itemList = new ItemList();
        itemList.id(UUID.fromString(itemEntity.getUuid()));
        itemList.itemName(itemEntity.getItemName());
        itemList.price(itemEntity.getPrice());
        final String itemType = itemEntity.getType().equals("0") ? "VEG" : "NON_VEG";
        itemList.itemType(ItemList.ItemTypeEnum.fromValue(itemType));
        return itemList;
    }

}

