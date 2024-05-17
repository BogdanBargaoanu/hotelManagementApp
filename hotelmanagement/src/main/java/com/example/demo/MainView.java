package com.example.demo;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

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
        grid.setColumns("name", "rooms", "latitude", "longitude");
        add(getForm(),grid);
    }

    private Component getForm() {
        var layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        layout.add(name, rooms, distance);
        return layout;
    }
    public void calculateDistances(double userLat, double userLon) {
        List<Hotel> hotels = hotelRepository.findAll();
        for (Hotel hotel : hotels) {
            double distance = Hotel.calculateDistance(userLat, userLon, hotel.getLatitude(), hotel.getLongitude());
            hotel.setDistanceToUser(distance);
        }
    }
}
