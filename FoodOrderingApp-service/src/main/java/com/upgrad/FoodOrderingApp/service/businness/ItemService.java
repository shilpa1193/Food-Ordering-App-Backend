package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     * Method tp retrieve item details using item uuid
     *
     * @param itemUuid - String represents item uuid
     * @return - item details using item uuid
     * @throws ItemNotFoundException - if no ItemEntity is found in the databse for the given item uuid
     */
    public ItemEntity getItemById(final String itemUuid) throws ItemNotFoundException {
        final ItemEntity itemEntity = itemDao.getItemById(itemUuid);

        // Throw exception if no item exists in the database for the given item UUID
        if (itemEntity == null) {
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }
        return itemEntity;
    }

    /**
     * Method to retrieve list of item details restaurantUuid and categoryUuid
     *
     * @param restaurantUuid - String represents restaurant uuid
     * @param categoryUuid   - String represents category uuid
     * @return - List of item details using restaurantUuid and categoryUuid
     */
    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantUuid, final String categoryUuid) {
        return itemDao.getItemsByCategoryAndRestaurant(restaurantUuid, categoryUuid);
    }

    /**
     * Method to retrieve the top five items of that restaurant based on the number of times that item was ordered
     *
     * @param restaurantEntity - RestaurantEntity object
     * @return - List of ItemEntity
     */
    public List<ItemEntity> getItemsByPopularity(final RestaurantEntity restaurantEntity) {
        return itemDao.getItemsByPopularity(restaurantEntity.getId());
    }
}
