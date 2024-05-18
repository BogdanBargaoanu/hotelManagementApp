package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.router.Route;
import org.hibernate.sql.exec.ExecutionException;
import com.vaadin.flow.component.UI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Route("")
@JsModule("./location.js")
public class MainView extends VerticalLayout{
    private HotelRepository hotelRepository;

    private TextField name = new TextField("Hotel name");
    private TextField rooms = new TextField("Number of rooms");
    private TextField distance = new TextField("Distance");
    private Button search = new Button("Search");
    private Grid<Hotel> grid = new Grid<>(Hotel.class);
    private Binder<Hotel> binder = new Binder<>(Hotel.class);
    private double userLatitude = 0;
    private double userLongitude = 0;

    public MainView(HotelRepository hotelRepository) {
        //getCurrentLocation();
        this.hotelRepository = hotelRepository;
        try {
            List<Hotel> hotels = readHotelsFromJson();
            calculateDistances(userLatitude, userLongitude, hotels);
            setRoomsCount(hotels);
            this.hotelRepository.saveAll(hotels);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        grid.setColumns("name", "roomsCount", "distance");
        grid.addComponentColumn(hotel -> {
            Button infoButton = new Button("Info");
            infoButton.addClickListener(event -> {
                infoButton.getUI().ifPresent(ui -> ui.navigate("hotel/" + hotel.getId()));
            });
            return infoButton;
        }).setHeader("Info");
        add(new H1("Hotel Management App"));
        HorizontalLayout layout = new HorizontalLayout(distance, search);
        layout.setAlignItems(Alignment.BASELINE);
        add(layout);
        add(grid);
        refreshGrid();

        search.addClickListener(event -> refreshGrid());
    }

    private void refreshGrid() {
        List<Hotel> allHotels = hotelRepository.findAll();
        if (distance.getValue() == null || distance.getValue().isEmpty()) {
            grid.setItems(allHotels);
        } else {
            double filterDistance = Double.parseDouble(distance.getValue());
            List<Hotel> filteredHotels = allHotels.stream()
                    .filter(hotel -> hotel.getDistance() <= filterDistance)
                    .collect(Collectors.toList());
            grid.setItems(filteredHotels);
        }
    }

    public static List<Hotel> readHotelsFromJson() throws IOException {
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
    public static void calculateDistances(double userLat, double userLon, List<Hotel> hotels) {
        for (Hotel hotel : hotels) {
            double distance = Hotel.calculateDistance(userLat, userLon, hotel.getLatitude(), hotel.getLongitude());
            hotel.setDistanceToUser(distance);
        }
    }
    public static void setRoomsCount(List<Hotel> hotels) {
        for (Hotel hotel : hotels) {
            hotel.setRoomsCount(hotel.getRooms().size());
        }
    }
    private void getCurrentLocation() {
        // Call the JavaScript function
        getElement().executeJs("window.getLocation($0, $1)",
                this.getElement().callJsFunction("onSuccess", this),
                this.getElement().callJsFunction("onError", this));
    }

    @ClientCallable
    public void onSuccess(double latitude, double longitude) {
        Notification.show("Current location: Latitude " + latitude + ", Longitude " + longitude);
    }

    @ClientCallable
    public void onError(String errorMessage) {
        Notification.show("Error getting location: " + errorMessage);
    }
}
