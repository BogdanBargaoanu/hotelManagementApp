package com.example.demo;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.transaction.Transactional;

@Route("hotel/:hotelId/room/:roomId")
@RouteAlias(value = "hotel/:hotelId/room/:roomId")
public class RoomBookingView extends VerticalLayout implements HasUrlParameter<String> {
    private HotelRepository hotelRepository;
    public RoomBookingView(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional
    public void setParameter(BeforeEvent event, @OptionalParameter String hotelId) {
        Location location = event.getLocation();
        String hotelIdParameter = location.getSegments().get(1); // hotelId is the second segment in the URL
        String roomIdParameter = location.getSegments().get(3); // roomId is the fourth segment in the URL

        try {
            int id = Integer.parseInt(hotelIdParameter);
            int roomNumber = Integer.parseInt(roomIdParameter);
            Hotel hotel = hotelRepository.findById(id).orElse(null);
            if (hotel != null) {
                Room room = hotel.findRoomByNumber(roomNumber);
                if (room != null) {
                    add(new H1("Room: " + room.getRoomNumber()));
                    add(new Paragraph("Type: " + room.getType()));
                    add(new Paragraph("Price: " + room.getPrice()));
                    add(new Paragraph("Available from: " + "to: "));
                    add(new Paragraph("Hotel: " + hotel.getName()));

                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
