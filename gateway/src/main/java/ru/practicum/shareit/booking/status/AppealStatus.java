package ru.practicum.shareit.booking.status;

import ru.practicum.shareit.exception.UnsupportedStatusException;

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

    public static void getStatus(String state) {
        int status;
        try {
            status = AppealStatus.valueOf(state).getAppealId();
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException(state);
        }
    }
}