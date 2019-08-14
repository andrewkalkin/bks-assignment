package ru.kalkin.bksassignment.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.zankowski.iextrading4j.api.refdata.v1.ExchangeSymbol;
import pl.zankowski.iextrading4j.api.stocks.BatchStocks;
import pl.zankowski.iextrading4j.api.stocks.Quote;
import ru.kalkin.bksassignment.exceptions.ValidatingException;
import ru.kalkin.bksassignment.model.Assets;
import ru.kalkin.bksassignment.model.Portfolio;
import ru.kalkin.bksassignment.model.Stock;
import ru.kalkin.bksassignment.model.StockQuote;
import ru.kalkin.bksassignment.services.QuotationService;
import ru.kalkin.bksassignment.validators.PortfolioValidator;
import ru.kalkin.bksassignment.validators.StockValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PortfolioController {

    private final QuotationService quotationService;
    @Qualifier("validationSource")
    private final ResourceBundleMessageSource messageSource;
    private final PortfolioValidator portfolioValidator;

    @InitBinder("portfolio")
    protected void initBinderStock(WebDataBinder binder) {
        binder.setValidator(portfolioValidator);
    }


    private void checkValidationResult(BindingResult bindingResult) throws ValidatingException {
        if (bindingResult.hasErrors()) {

            StringBuilder stringBuilder = new StringBuilder();

            bindingResult.getAllErrors().forEach(error ->
                    stringBuilder.append(String.format("%s\n", messageSource.getMessage(error, Locale.getDefault())))
            );

            throw new ValidatingException(stringBuilder.toString());
        }
    }

    @PostMapping("/assets")
    public Assets getPortfolioAssets(@Valid @RequestBody Portfolio portfolio, BindingResult bindingResult) throws ValidatingException{
        checkValidationResult(bindingResult);
        return quotationService.getAssets(portfolio);
    }

    @ExceptionHandler(ValidatingException.class)
    public String symbolNotFoundMessage(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler({JsonParseException.class, InvalidFormatException.class})
    public String errorParsing() {
        return "Error parsing json object";
    }

    @ExceptionHandler(Exception.class)
    public String internalError() {
        return "Something gone wrong...";
    }


}
