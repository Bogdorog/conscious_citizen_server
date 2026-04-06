package com.sergeev.conscious_citizen_server.exception;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) { super(message); }
}
