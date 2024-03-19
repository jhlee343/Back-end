package shootingstar.var.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import shootingstar.var.annotation.After30Days;

@Slf4j
public class After30DaysValidator implements ConstraintValidator<After30Days, String> {

    @Override
    public void initialize(After30Days constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String dateTimeString, ConstraintValidatorContext context) {
        LocalDateTime promiseDate = LocalDateTime.parse(dateTimeString);
        LocalDateTime currentDate =  LocalDateTime.now();
        LocalDateTime standardDate = LocalDateTime.of(currentDate.getYear(), currentDate.getMonth(), currentDate.getDayOfMonth(), 0, 0, 0);
        return promiseDate.isAfter(standardDate.plusDays(30));
    }
}
