package vn.com.vpbanks.loyalty.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

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
                    field.set(target, field.get(source));
                }
            }
            return target;
        } catch (Exception e) {
            log.error("Error - {}", e.getMessage());
        }

        return null;
    }

    public static void main(String[] args) {
        File file = new File("./application-dev.yaml");
        System.out.println(file.getName());
        System.out.println(Optional.ofNullable(file.getName())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(file.getName().lastIndexOf(".") + 1)));
    }
}
