package com.example.demo;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.example.demo.MainView.*;

@Route("hotel/:hotelId")
@RouteAlias(value = "hotel/:hotelId")
public class HotelRoomView extends VerticalLayout implements HasUrlParameter<String> {
    private HotelRepository hotelRepository;
    public HotelRoomView(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
        try {
            List<Hotel> hotels = readHotelsFromJson();
            this.hotelRepository.saveAll(hotels);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    @Transactional
    public void setParameter(BeforeEvent event, @OptionalParameter String hotelId) {
        Location location = event.getLocation();
        String hotelIdParameter = location.getSegments().get(1); // Assuming hotelId is the second segment in the URL

        try {
            int id = Integer.parseInt(hotelIdParameter);
            Hotel hotel = hotelRepository.findById(id).orElse(null);
            if (hotel != null) {
                add(new H1("Hotel: " + hotel.getName()));
                for (Room room : hotel.getRooms()) {
                    add(new Paragraph("Room: " + room.getRoomNumber()));
                }
            } else {
                add(new H1("Hotel not found"));
            }
        } catch (NumberFormatException e) {
            add(new H1("Invalid Hotel ID"));
        }
    }
}
