package net.starly.whitelist.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MessageType {

    NORMAL("messages"),
    ERROR("errorMessages");

    @Getter private final String path;
}
