package com.example.EWeb.service;

import com.example.EWeb.model.Category;
import com.example.EWeb.model.Item;
import com.example.EWeb.model.User;
import com.example.EWeb.repository.CategoryRepository;
import com.example.EWeb.repository.ItemRepository;
import com.example.EWeb.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(String id) { // Changed to String
        return itemRepository.findById(id);
    }

    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategory_NameIgnoreCase(category);
    }

    @Transactional
    public Item saveItem(Item item) {
        // Handle category
        Category incoming = item.getCategory();
        if (incoming != null) {
            Category attachedCategory = null;

            if (incoming.getCategoryId() != null && !incoming.getCategoryId().trim().isEmpty()) {
                attachedCategory = categoryRepository.findById(incoming.getCategoryId()).orElse(null);
            }

            if (attachedCategory == null && incoming.getName() != null && !incoming.getName().trim().isEmpty()) {
                attachedCategory = categoryRepository.findByNameIgnoreCase(incoming.getName()).orElse(null);
            }

            if (attachedCategory == null) {
                if (incoming.getCategoryId() == null || incoming.getCategoryId().trim().isEmpty()) {
                    String generatedId = "CAT" + System.currentTimeMillis();
                    incoming.setCategoryId(generatedId.substring(0, Math.min(generatedId.length(), 10)));
                }
                attachedCategory = categoryRepository.save(incoming);
            }

            item.setCategory(attachedCategory);
        }

        // Handle user (if provided)
        User incomingUser = item.getUser();
        if (incomingUser != null && incomingUser.getUserId() != null) {
            User attachedUser = userRepository.findById(incomingUser.getUserId()).orElse(null);
            item.setUser(attachedUser);
        }

        // Generate Item ID if not provided
        if (item.getItemId() == null || item.getItemId().trim().isEmpty()) {
            String generatedId = "ITEM" + System.currentTimeMillis();
            item.setItemId(generatedId.substring(0, Math.min(generatedId.length(), 10)));
        }

        return itemRepository.save(item);
    }

    public void deleteItem(String id) { // Changed to String
        itemRepository.deleteById(id);
    }
}