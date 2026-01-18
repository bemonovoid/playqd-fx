package io.playqd.controller;

import io.playqd.client.GetSearchResponse;
import io.playqd.client.PageRequest;
import io.playqd.data.SearchFlag;
import io.playqd.data.SearchRequestParams;
import io.playqd.service.SearchEngine;
import io.playqd.service.SearchEngineImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Collections;
import java.util.HashSet;

public abstract class AbstractSearchController {

    protected SearchEngine searchEngine;

    protected void initializeInternal() {
        this.searchEngine = new SearchEngineImpl();
    }

    protected GetSearchResponse search(SearchRequestParams params) {
        return search(params, 0 , 50);
    }

    protected GetSearchResponse search(SearchRequestParams params, int pageIdx, int pageSize) {
        var pageRequest = new PageRequest(pageIdx, pageSize);
        return searchEngine.search(params, pageRequest);
    }

    protected abstract SearchRequestParams getSearchRequestParams();

    protected abstract int getPageSize();

}
