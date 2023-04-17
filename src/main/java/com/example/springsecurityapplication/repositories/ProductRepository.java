package com.example.springsecurityapplication.repositories;

import com.example.springsecurityapplication.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {


    // ищем все продукты названию/части названия продукта, регистр любой
    List<Product> findByTitleContainingIgnoreCase(String name);

    // ищем по наименованию + фильтруем по диапазону цены
    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3)", nativeQuery = true)
    List<Product> findByTitleAndPriceGreaterThanEqualAndPriceLessThanEqual(String title, float from, float to);

    // ищем по наименованию + фильтруем по диапазону цены + сортируем по возрастанию цены
    @Query(value = "select * from product where (lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1') and (price >= ?2 and price <= ?3) order by price", nativeQuery = true)
    List<Product> findByTitleOrderByPriceAsc(String title, float from, float to);

    // ищем по наименованию + фильтруем по диапазону цены + сортируем по убыванию цены
    @Query(value = "select * from product where (lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1') and (price >= ?2 and price <= ?3) order by price desc", nativeQuery = true)
    List<Product> findByTitleOrderByPriceDesc(String title, float from, float to);

    // ищем по наименованию + фильтруем по диапазону цены + сортируем по возрастанию цены + фильтруем по категории
    @Query(value = "select * from product where category_id = ?4 and(lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1') and (price >= ?2 and price <= ?3) order by price", nativeQuery = true)
    List<Product> findByTitleAndCategoryOrderByPriceAsc(String title, float from, float to, int category);

    // ищем по наименованию + фильтруем по диапазону цены + сортируем по убыванию цены + фильтруем по категории
    @Query(value = "select * from product where category_id = ?4 and(lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1') and (price >= ?2 and price <= ?3) order by price desc", nativeQuery = true)
    List<Product> findByTitleAndCategoryOrderByPriceDesc(String title, float from, float to, int category);
}

