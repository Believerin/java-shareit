package ru.practicum.shareit.booking.status;

public enum AppealStatus {
    ALL(1),
    CURRENT(2),
    PAST(3),
    FUTURE(4),
    WAITING(5),
    REJECTED(6);

    private final int appealId;

    AppealStatus(int appealId) {
        this.appealId = appealId;
    }

    public int getAppealId() {
        return appealId;
    }
}
