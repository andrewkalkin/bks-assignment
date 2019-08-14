package ru.kalkin.bksassignment.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.kalkin.bksassignment.model.Portfolio;
import ru.kalkin.bksassignment.model.Stock;

import java.util.Objects;


@Component
public class PortfolioValidator implements Validator {


    private final StockValidator stockValidator;

    public PortfolioValidator(StockValidator stockValidator) {

        if (stockValidator == null) {
            throw new IllegalArgumentException("The supplied [Validator] is " +
                    "required and must not be null.");
        }
        if (!stockValidator.supports(Stock.class)) {
            throw new IllegalArgumentException("The supplied [Validator] must " +
                    "support the validation of [Stock] instances.");
        }
        this.stockValidator = stockValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Portfolio.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Portfolio portfolio = (Portfolio) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "stocks", "field.empty");

        if (Objects.isNull(errors.getFieldError("stocks"))) {

            long i = 0;

            for (Stock stock: portfolio.getStocks()) {
                errors.setNestedPath(String.format("stocks[%d]", i));
                ValidationUtils.invokeValidator(this.stockValidator, stock, errors);
                i++;
            }

//            portfolio.getStocks().forEach(stock -> {
//                    errors.setNestedPath(String.format("stocks[%d]", i));
//                    ValidationUtils.invokeValidator(this.stockValidator, stock, errors);
//            });
        }

    }
}
