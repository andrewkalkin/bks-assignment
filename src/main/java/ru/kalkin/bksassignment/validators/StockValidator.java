package ru.kalkin.bksassignment.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.kalkin.bksassignment.model.Stock;
import ru.kalkin.bksassignment.services.QuotationService;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class StockValidator implements Validator {

    private final QuotationService quotationService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Stock.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Stock stock = (Stock) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "symbol", "field.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "volume", "field.empty");


        String [] args = { stock.getSymbol().trim().toUpperCase() };

        if (
                Objects.isNull(errors.getFieldError("symbol")) &&
                !quotationService.getSymbolsList().contains(stock.getSymbol().trim().toUpperCase())
        ) {
            errors.rejectValue("symbol", "field.notfound", args,"not found");
        }


        if (
                Objects.isNull(errors.getFieldError("volume")) &&
                        Objects.nonNull(stock.getVolume()) &&
                        (stock.getVolume().scale() != 0 || stock.getVolume().signum() != 1)
        ) {
            errors.rejectValue("volume","field.incorrect", args, "incorrect value");
        }

    }
}
