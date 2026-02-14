package io.playqd.controller;

import io.playqd.client.GetSearchResponse;
import io.playqd.client.PageRequest;
import io.playqd.data.SearchFlag;
import io.playqd.data.SearchRequestParams;
import io.playqd.service.SearchEngine;
import io.playqd.service.SearchEngineImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public abstract class AbstractSearchController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSearchController.class);

    protected SearchEngine searchEngine;
    protected final ObservableSet<SearchFlag> searchFlags = FXCollections.observableSet(new HashSet<>());

    public AbstractSearchController() {
        this.searchEngine = new SearchEngineImpl();
    }

    protected final GetSearchResponse performSearch(SearchRequestParams params, int pageIdx, int pageSize) {
        var pageRequest = new PageRequest(pageIdx, pageSize);
        return searchEngine.search(params, pageRequest);
    }

}
