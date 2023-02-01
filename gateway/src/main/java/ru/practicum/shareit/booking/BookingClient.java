package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoCreated;
import ru.practicum.shareit.booking.status.AppealStatus;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> acceptOrDeny(int bookingId, boolean approved, int userId) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters);
    }

    public ResponseEntity<Object> get(int userId, int bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByBooker(int bookerId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", AppealStatus.valueOf(state),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> getAllByOwner(int bookerId, String state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", AppealStatus.valueOf(state),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", bookerId, parameters);
    }

    public ResponseEntity<Object> add(BookingDtoCreated bookingDtoCreated, int userId) {
        return post("", userId, bookingDtoCreated);
    }
}
