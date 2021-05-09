package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


/**
 * The CategoryEntity class is mapped to table 'category' in database
 * All the columns are mapped to its respective attributes of the class
 */

@Entity
@Table(name = "category")
@NamedQueries(
        {
                @NamedQuery(name = "allCategoriesOrderedByName",
                        query = "select c from CategoryEntity c order by c.categoryName"),
                @NamedQuery(name = "categoryByUuid",
                        query = "select c from CategoryEntity c where c.uuid = :categoryUuid")
        }
)
public class CategoryEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "CATEGORY_NAME")
    @Size(max = 255)
    private String categoryName;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<ItemEntity> items;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<RestaurantEntity> restaurants;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

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

    public List<ItemEntity> getItems() {
        return items;
    }

    public void setItems(List<ItemEntity> items) {
        this.items = items;
    }

    public List<RestaurantEntity> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantEntity> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CategoryEntity that = (CategoryEntity) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(uuid, that.uuid)
                .append(categoryName, that.categoryName)
                .append(items, that.items)
                .append(restaurants, that.restaurants)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(uuid)
                .append(categoryName)
                .append(items)
                .append(restaurants)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("uuid", uuid)
                .append("categoryName", categoryName)
                .append("items", items)
                .append("restaurants", restaurants)
                .toString();
    }
}
