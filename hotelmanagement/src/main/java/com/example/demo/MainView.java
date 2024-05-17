package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Route("")
public class MainView extends VerticalLayout{
    private HotelRepository hotelRepository;

    private TextField name = new TextField("Hotel name");
    private TextField rooms = new TextField("Number of rooms");
    private TextField distance = new TextField("Distance");
    private Grid<Hotel> grid = new Grid<>(Hotel.class);
    private Binder<Hotel> binder = new Binder<>(Hotel.class);

    public MainView(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
        try {
            List<Hotel> hotels = readHotelsFromJson();
            calculateDistances(37.7749, -122.4194, hotels);
            setRoomsCount(hotels);
            this.hotelRepository.saveAll(hotels);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        grid.setColumns("name", "roomsCount", "distance");
        add(grid);
        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(hotelRepository.findAll());
    }

    private List<Hotel> readHotelsFromJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = new FileInputStream(new File("E:\\hotelManagementApp\\hotelmanagement\\src\\main\\resources\\static\\hotels.json"));
        return mapper.readValue(inputStream, mapper.getTypeFactory().constructCollectionType(List.class, Hotel.class));
    }
    private Component getForm() {
        var layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        layout.add(name, rooms, distance);
        return layout;
    }
    public void calculateDistances(double userLat, double userLon, List<Hotel> hotels) {
        for (Hotel hotel : hotels) {
            double distance = Hotel.calculateDistance(userLat, userLon, hotel.getLatitude(), hotel.getLongitude());
            hotel.setDistanceToUser(distance);
        }
    }
    public void setRoomsCount(List<Hotel> hotels) {
        for (Hotel hotel : hotels) {
            hotel.setRoomsCount(hotel.getRooms().size());
        }
    }
}
