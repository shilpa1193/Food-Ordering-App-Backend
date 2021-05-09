package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to get all restaurants in order of their ratings
     *
     * @return - list of Restaurant Entities in order of their ratings
     */
    public List<RestaurantEntity> getAllRestaurants() {
        return entityManager.createNamedQuery("allRestaurants", RestaurantEntity.class).getResultList();
    }

    /**
     * Method to get all restaurants using restaurant name
     *
     * @param restaurantName - String representing restaurant name
     * @return - list of Restaurant Entities using restaurant name
     */
    public List<RestaurantEntity> restaurantsByName(final String restaurantName) {
        return entityManager.createNamedQuery("restaurantsByName", RestaurantEntity.class)
                .setParameter("restaurantName", "%" + restaurantName.toLowerCase() + "%")
                .getResultList();
    }

    /**
     * Method to get all restaurants category entity using category uuid
     *
     * @param categoryUuid - String representing Category UUID
     * @return - list of get all restaurants category entity using category uuid
     */
    public List<RestaurantCategoryEntity> restaurantsByCategoryId(final String categoryUuid) {
        return entityManager.createNamedQuery("restaurantsByCategory", RestaurantCategoryEntity.class)
                .setParameter("categoryUuid", categoryUuid)
                .getResultList();
    }

    /**
     * Method to retrieve RestaurantEntity for the given restaurant UUID
     *
     * @param restaurantUuid - String representing restaurant UUID
     * @return RestaurantEntity if data exists in the database, else return null
     */
    public RestaurantEntity restaurantByUUID(final String restaurantUuid) {
        try {
            return entityManager.createNamedQuery("restaurantByUuid", RestaurantEntity.class)
                    .setParameter("restaurantUuid", restaurantUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to update RestaurantEntity in the database
     * @param restaurantEntity - RestaurantEntity object
     * @return - updated RestaurantEntity
     */
    public RestaurantEntity updateRestaurantEntity(final RestaurantEntity restaurantEntity) {
        return entityManager.merge(restaurantEntity);
    }
}
