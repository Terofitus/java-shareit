package ru.practicum.shareit.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartEndTimeValidator.class)
@Documented
public @interface StartEndTime {
    String message() default "{StartEndTime.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
