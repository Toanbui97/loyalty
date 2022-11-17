package vn.com.vpbanks.loyalty.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@UtilityClass
@Slf4j
public class ObjectUtil {

    public <S, T> T mergeObject(S source, T target) {
        try {
            for (Field field : source.getClass().getFields()) {
                field.setAccessible(true);
                if (field.get(source) != null
                        && target.getClass().getField(field.getName()) != null) {
                    field.set(target, field.get(source));
                }
            }
            return target;
        } catch (Exception e) {
            log.error("Error - {}", e.getMessage());
        }
        return null;
    }
}
