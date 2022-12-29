package vn.com.loyalty.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class ObjectUtil {


    public <S, T> T mergeObject(S source, T target) {
        try {
            List<String> targetFields = Arrays.stream(target.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
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
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String s = "a";
        s.concat("b");
        System.out.println(s);
    }

}
