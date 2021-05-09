package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * The RestaurantEntity class is mapped to table 'restaurant' in database
 * All the columns are mapped to its respective attributes of the class
 */

@Entity
@Table(name = "restaurant")
@NamedQueries(
        {
                @NamedQuery(name = "allRestaurants",
                        query = "select r from RestaurantEntity r order by r.customerRating desc"),
                @NamedQuery(name = "restaurantsByName",
                        query = "select r from RestaurantEntity  r where lower(r.restaurantName) like :restaurantName order by r.restaurantName"),
                @NamedQuery(name = "restaurantByUuid",
                        query = "select r from RestaurantEntity r where r.uuid = :restaurantUuid")
        }
)
public class RestaurantEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "RESTAURANT_NAME")
    @NotNull
    @Size(max = 50)
    private String restaurantName;

    @Column(name = "PHOTO_URL")
    @Size(max = 255)
    private String photoUrl;

    @Column(name = "CUSTOMER_RATING")
    @NotNull
    private Double customerRating;

    @Column(name = "NUMBER_OF_CUSTOMERS_RATED")
    @NotNull
    private Integer numberCustomersRated;

    @Column(name = "AVERAGE_PRICE_FOR_TWO")
    @NotNull
    private Integer avgPrice;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "restaurant_category",
            joinColumns = @JoinColumn(name = "RESTAURANT_ID", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "id", nullable = false))
    private List<CategoryEntity> categories;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "restaurant_item",
            joinColumns = @JoinColumn(name = "RESTAURANT_ID", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID", referencedColumnName = "id", nullable = false))
    private List<ItemEntity> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Double getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Double customerRating) {
        this.customerRating = customerRating;
    }

    public Integer getNumberCustomersRated() {
        return numberCustomersRated;
    }

    public void setNumberCustomersRated(Integer numberCustomersRated) {
        this.numberCustomersRated = numberCustomersRated;
    }

    public Integer getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Integer avgPrice) {
        this.avgPrice = avgPrice;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RestaurantEntity that = (RestaurantEntity) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(uuid, that.uuid)
                .append(restaurantName, that.restaurantName)
                .append(photoUrl, that.photoUrl)
                .append(customerRating, that.customerRating)
                .append(numberCustomersRated, that.numberCustomersRated)
                .append(avgPrice, that.avgPrice)
                .append(address, that.address)
                .append(categories, that.categories)
                .append(items, that.items)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(uuid)
                .append(restaurantName)
                .append(photoUrl)
                .append(customerRating)
                .append(numberCustomersRated)
                .append(avgPrice)
                .append(address)
                .append(categories)
                .append(items)
                .toHashCode();
    }

    @Override
    public String toString() {

        return new ToStringBuilder(this)
                .append("id", id)
                .append("uuid", uuid)
                .append("restaurantName", restaurantName)
                .append("photoUrl", photoUrl)
                .append("customerRating", customerRating)
                .append("numberCustomersRated", numberCustomersRated)
                .append("avgPrice", avgPrice)
                .append("address", address)
                .append("categories", categories)
                .append("items", items)
                .toString();
    }

}
