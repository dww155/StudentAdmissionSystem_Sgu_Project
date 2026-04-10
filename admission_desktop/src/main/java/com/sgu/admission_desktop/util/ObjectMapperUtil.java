package com.sgu.admission_desktop.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperUtil {
    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
}
