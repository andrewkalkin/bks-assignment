package ru.kalkin.bksassignment.requests;

import pl.zankowski.iextrading4j.api.stocks.BatchStocks;
import pl.zankowski.iextrading4j.client.rest.manager.RestRequest;
import pl.zankowski.iextrading4j.client.rest.manager.RestRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.AbstractBatchStocksRequestBuilder;

import javax.ws.rs.core.GenericType;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;

public class BatchMarketStocksRequestBuilder extends AbstractBatchStocksRequestBuilder<Map<String, BatchStocks>,
        BatchMarketStocksRequestBuilder> {

    private Set<String> symbols = new HashSet<>();

    public BatchMarketStocksRequestBuilder withSymbol(final String symbol) {
        this.symbols.add(symbol);
        return this;
    }

    public BatchMarketStocksRequestBuilder withSymbols(final List<String> symbols) {
        this.symbols.addAll(symbols);
        return this;
    }

    protected void processSymbols() {
        this.queryParameters.put("symbols", symbols.stream()
                .collect(joining(",")));
    }

    Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    @Override
    public RestRequest<Map<String, BatchStocks>> build() {
        processTypes();
        processSymbols();
        return RestRequestBuilder.<Map<String, BatchStocks>>builder()
                .withPath("/stock/{symbol}/batch")
                .addPathParam("symbol", "market").get()
                .withResponse(new GenericType<Map<String, BatchStocks>>() {})
                .addQueryParam(getQueryParameters())
                .build();
    }

}

