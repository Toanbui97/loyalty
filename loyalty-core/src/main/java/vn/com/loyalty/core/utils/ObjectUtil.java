package vn.com.loyalty.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@UtilityClass
@Slf4j
public class ObjectUtil {


    public <S, T> T mergeObject(S source, T target) {
        try {
            List<String> targetFields = Arrays.stream(target.getClass().getDeclaredFields()).map(Field::getName).toList();
            for (Field field : source.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(source) != null
                        && targetFields.contains(field.getName())) {

                    Object sourceValue = field.get(source);
                    Field targetField = target.getClass().getDeclaredField(field.getName());
                    targetField.setAccessible(true);
                    targetField.set(target, sourceValue);
                    targetField.setAccessible(false);
                }
                field.setAccessible(false);
            }
            return target;
        } catch (Exception e) {
            log.error("Error - {}", e.getMessage());
        }

        return null;
    }

    public String prettyPrintJsonObject(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }
}
