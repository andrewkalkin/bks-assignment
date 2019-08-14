package ru.kalkin.bksassignment.services;

import ru.kalkin.bksassignment.model.Assets;
import ru.kalkin.bksassignment.model.Portfolio;

import java.util.List;

public interface QuotationService {

    Assets getAssets(Portfolio portfolio);
    List<String> getSymbolsList();

}
