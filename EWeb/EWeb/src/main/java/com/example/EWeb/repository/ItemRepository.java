package com.example.EWeb.repository;

import com.example.EWeb.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, String> { // Changed ID type to String
    List<Item> findByCategory_NameIgnoreCase(String categoryName);
    List<Item> findByUser_UserId(Integer userId); // Add user-specific queries
}