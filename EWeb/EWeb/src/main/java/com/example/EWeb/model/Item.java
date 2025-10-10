package com.example.EWeb.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Item") // Match your actual table name
public class Item {

    @Id
    @Column(name = "Item_ID", length = 10) // Changed to String to match DB
    private String itemId;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Item_Description")
    private String description;

    @Column(name = "Image_URL")
    private String imageUrl;

    // Add User relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Many items belong to one category
    @ManyToOne
    @JoinColumn(name = "Category_ID", referencedColumnName = "Category_ID")
    private Category category;

    @Column(name = "StartingPrice")
    private BigDecimal startingPrice;

    @Column(name = "CurrentHighestBid")
    private BigDecimal currentHighestBid;

    @Column(name = "Status")
    private String status;

    @Column(name = "Deadline")
    private LocalDateTime deadline;

    // Constructors
    public Item() {}

    public Item(String itemId, String title, String description, String imageUrl,
                User user, Category category, BigDecimal startingPrice,
                BigDecimal currentHighestBid, String status, LocalDateTime deadline) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.user = user;
        this.category = category;
        this.startingPrice = startingPrice;
        this.currentHighestBid = currentHighestBid;
        this.status = status;
        this.deadline = deadline;
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }

    public BigDecimal getCurrentHighestBid() { return currentHighestBid; }
    public void setCurrentHighestBid(BigDecimal currentHighestBid) { this.currentHighestBid = currentHighestBid; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
}