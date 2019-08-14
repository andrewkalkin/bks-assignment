package ru.kalkin.bksassignment.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.zankowski.iextrading4j.api.refdata.v1.ExchangeSymbol;
import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.rest.request.refdata.v1.SymbolsRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;
import ru.kalkin.bksassignment.model.*;
import ru.kalkin.bksassignment.requests.CachingStocksBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationServiceImpl implements QuotationService {

    private final IEXCloudClient cloudClient;
    private final CachingStocksBean cachingStocksBean;

    private List<String> cachedSymbols;

    private Map<String, StockQuote> getStockQuotes(List<String> symbols) {
        Map<String, StockQuote> stockQuotes = new HashMap<>();
        symbols.forEach(symbol -> stockQuotes.put(symbol.toUpperCase(), cachingStocksBean.getStockQuote(symbol)));
        return stockQuotes;
    }

    @Override
    public Assets getAssets(Portfolio portfolio) {

        Map<String, StockQuote> stockQuotes = getStockQuotes(portfolio.getStocks().stream().map(Stock::getSymbol).map(String::toLowerCase).collect(Collectors.toList()));
        portfolio.getStocks().forEach(stock -> stockQuotes.get(stock.getSymbol().toUpperCase()).setValue(
                stock.getVolume().multiply(stockQuotes.get(stock.getSymbol().toUpperCase()).getLatestPrice()))
        );

        Assets assets = new Assets();
        assets.setAllocations(new LinkedList<>());

        Map<String, BigDecimal> sectorVolumes = new HashMap<>();
        stockQuotes.values().forEach(value -> sectorVolumes.merge(value.getSector(), value.getValue(), BigDecimal::add));

        assets.setValue(sectorVolumes.values().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO));

        sectorVolumes.forEach(
                (sector, volume) -> assets.getAllocations().add(
                        Allocation.builder()
                                .sector(sector)
                                .assetValue(volume.setScale(0, RoundingMode.HALF_UP))
                                .proportion(volume.divide(assets.getValue(), 3, RoundingMode.HALF_UP))
                                .build())
        );

        assets.setValue(assets.getValue().setScale(0, RoundingMode.HALF_UP));
        return assets;
    }

    @Scheduled(fixedRate = 3600000)
    private void refreshSymbols() {
        cachedSymbols = cloudClient.executeRequest(new SymbolsRequestBuilder().build())
                .stream()
                .map(ExchangeSymbol::getSymbol)
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getSymbolsList() {
        return cachedSymbols;
    }

}
