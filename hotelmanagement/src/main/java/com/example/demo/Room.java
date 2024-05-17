package com.example.demo;

import jakarta.persistence.*;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private int roomNumber;
    private int type; // You can define an enum for room types (single, double, suite)
    private double price;
    private boolean isAvailable;

    @ManyToOne
    private Hotel hotel;

    // Getters and setters for all fields

    public Room(int roomNumber, int type, double price, boolean isAvailable) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    // Method to mark a room as unavailable
    public void setUnavailable() {
        this.isAvailable = false;
    }

    // Method to mark a room as available
    public void setAvailable() {
        this.isAvailable = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
