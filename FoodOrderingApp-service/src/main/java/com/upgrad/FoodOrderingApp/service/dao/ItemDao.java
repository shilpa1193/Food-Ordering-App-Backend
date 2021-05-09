package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get  item details using item uuid
     *
     * @param itemUuid - String represents item uuid
     * @return - item details using item uuid
     */
    public ItemEntity getItemById(final String itemUuid) {
        try {
            return entityManager.createNamedQuery("itemById", ItemEntity.class)
                    .setParameter("itemUuid", itemUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to retrieve the top five items of that restaurant based on the number of times that item was ordered
     *
     * @param restaurantId - Restaurant Id
     * @return - List of ItemEntity
     */
    public List<ItemEntity> getItemsByPopularity(final Integer restaurantId) {
        return entityManager.createNamedQuery("topFivePopularItemsByRestaurant", ItemEntity.class)
                .setParameter(0, restaurantId)
                .getResultList();
    }

    /**
     * Method to retrieve all items for a category of a restaurant
     *
     * @param restaurantUuid - Restaurant UUID
     * @param categoryUuid   - Category UUID
     * @return List of ItemEntity
     */
    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantUuid, final String categoryUuid) {
        return entityManager.createNamedQuery("itemsByCategoryByRestaurant", ItemEntity.class)
                .setParameter("restaurantUuid", restaurantUuid)
                .setParameter("categoryUuid", categoryUuid)
                .getResultList();
    }
}
