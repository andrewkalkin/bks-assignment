package ru.kalkin.bksassignment.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.CompanyRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;
import ru.kalkin.bksassignment.model.StockQuote;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CachingStocksBean {

    private final IEXCloudClient cloudClient;

    @Cacheable("stockQuotesRequests")
    public StockQuote getStockQuote(String symbol) {
        BigDecimal latestPrice = cloudClient.executeRequest(new QuoteRequestBuilder().withSymbol(symbol.toLowerCase()).build()).getLatestPrice();
        String sector = cloudClient.executeRequest(new CompanyRequestBuilder().withSymbol(symbol.toLowerCase()).build()).getSector();
        return StockQuote.builder().latestPrice(latestPrice).sector(sector).build();
    }
}
