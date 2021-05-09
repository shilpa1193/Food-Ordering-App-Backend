package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * RestController method called when the request pattern is of type "/category"
     * and the incoming request is of 'GET' type
     * Retrieve category list order by name
     *
     * @return - ResponseEntity(CategoriesListResponse, HttpStatus.OK)
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {
        final List<CategoryEntity> allCategories = categoryService.getAllCategoriesOrderedByName();

        final CategoriesListResponse categoriesListResponse = new CategoriesListResponse();
        if (!allCategories.isEmpty()) {
            final List<CategoryListResponse> categoryLists = new ArrayList<>();
            allCategories.forEach(
                    category -> categoryLists.add(createCategoryList(category))
            );
            categoriesListResponse.categories(categoryLists);
        }

        return new ResponseEntity<>(categoriesListResponse, HttpStatus.OK);

    }

    /**
     * RestController method called when the request pattern is of type "/category/{category_id}"
     * and the incoming request is of 'GET' type
     * Retrieve coupon details using coupon name
     *
     * @param categoryId - This represents coupon name
     * @return - ResponseEntity(CouponDetailsResponse, HttpStatus.OK)
     * @throws CategoryNotFoundException - if incorrect/ invalid category id is sent,
     *                                   or the category id doesn't exist
     */
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(
            @PathVariable("category_id") final String categoryId) throws CategoryNotFoundException {
        final CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);
        final List<ItemEntity> itemEntities = categoryEntity.getItems();

        final CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse()
                .id(UUID.fromString(categoryEntity.getUuid()))
                .categoryName(categoryEntity.getCategoryName());

        if (!itemEntities.isEmpty()) {
            final List<ItemList> itemLists = new ArrayList<>();
            itemEntities.forEach(itemEntity -> itemLists.add(createItemList(itemEntity)));
            categoryDetailsResponse.itemList(itemLists);
        }

        return new ResponseEntity<>(categoryDetailsResponse, HttpStatus.OK);
    }

    /**
     * Method to set and return ItemList
     *
     * @param itemEntity - ItemEntity object
     * @return - ItemList
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

    /**
     * Method to set all  field into CategoryListResponse
     *
     * @param categoryEntity - CategoryEntity object
     * @return - CategoryListResponse
     */
    private CategoryListResponse createCategoryList(final CategoryEntity categoryEntity) {
        final CategoryListResponse categoryListResponse = new CategoryListResponse();
        categoryListResponse.categoryName(categoryEntity.getCategoryName());
        categoryListResponse.id(UUID.fromString(categoryEntity.getUuid()));
        return categoryListResponse;
    }
}
