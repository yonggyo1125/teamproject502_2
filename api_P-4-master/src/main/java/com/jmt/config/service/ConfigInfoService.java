package com.jmt.config.service;

import lombok.RequiredArgsConstructor;

//@Service
@RequiredArgsConstructor
public class ConfigInfoService {
/*
    private final ConfigsRepository repository;

    public <T> Optional<T> get(String code, Class<T> clazz) {
        return get(code, clazz, null);
    }

    public <T> Optional<T> get(String code, TypeReference<T> typeReference) {
        return get(code, null, typeReference);
    }

    public <T> Optional<T> get(String code, Class<T> clazz, TypeReference<T> typeReference) {
        Configs config = repository.findById(code).orElse(null);
        if (config == null || !StringUtils.hasText(config.getData())) {
            return Optional.ofNullable(null);
        }

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());

        String jsonString = config.getData();
        try {
            T data = null;
            if (clazz == null) { // TypeRefernce로 처리
                data = om.readValue(jsonString, new TypeReference<T>() {});
            } else { // Class로 처리
                data = om.readValue(jsonString, clazz);
            }
            return Optional.ofNullable(data);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
            return Optional.ofNullable(null);
        }
    }

 */
}
