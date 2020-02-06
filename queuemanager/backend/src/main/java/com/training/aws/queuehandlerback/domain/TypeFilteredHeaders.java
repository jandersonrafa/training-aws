package com.training.aws.queuehandlerback.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum TypeFilteredHeaders {

    ID("id", "String"),
    TIMESTAMP("timestamp", "Number.java.lang.Long"),
    CONTENT_TYPE("contentType", "String"),
    LOGGED_USER_ID("LoggedUserId", "Number.java.lang.Long"),
    SENT_TIMESTAMP("SentTimestamp", "String"),
    RECEIPT_HANDLE("ReceiptHandle", "String"),
    LOOKUP_DESTINATION("lookupDestination", "String"),
    ORIGINAL_SENT_TIMESTAMP("SentTimestampOriginal", "String"),
    SYSTEM_LOCALE_LANGUAGE("SystemLocaleLanguage", "String"),
    SYSTEM_LOCALE_COUNTRY("SystemLocaleCountry", "String");

    private String name;
    private String type;

    public static Optional<TypeFilteredHeaders> getByName(String name) {
        return Stream.of(TypeFilteredHeaders.values()).filter(t -> t.getName().equals(name)).findFirst();
    }
}
