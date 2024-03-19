package shootingstar.var.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import shootingstar.var.annotation.After30Days;

public class After30DaysValidator implements ConstraintValidator<After30Days, LocalDateTime> {

    @Override
    public void initialize(After30Days constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
       LocalDateTime currentDate =  LocalDateTime.of(value.getYear(), value.getMonth(), value.getDayOfMonth(), 0, 0, 0);
       return value.isAfter(currentDate.plusDays(30));
    }
}
