package ru.practicum.shareit.booking.status;

public enum BookingStatus {
    WAITING(1),
    APPROVED(2),
    REJECTED(3),
    CANCELLED(4);

    private final int bookingId;

    BookingStatus(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getBookingId() {
        return bookingId;
    }
}