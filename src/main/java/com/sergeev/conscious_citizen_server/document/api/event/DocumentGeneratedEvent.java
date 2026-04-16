package com.sergeev.conscious_citizen_server.document.api.event;

public record DocumentGeneratedEvent(
  Long id,
  String filePath
) {}