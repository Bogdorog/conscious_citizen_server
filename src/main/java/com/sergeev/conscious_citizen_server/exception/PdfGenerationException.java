package com.sergeev.conscious_citizen_server.exception;

public class PdfGenerationException extends RuntimeException {
    public PdfGenerationException(String message, Throwable cause) { super(message, cause); }
}
