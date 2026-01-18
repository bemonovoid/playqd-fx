package io.playqd.service;

import io.playqd.client.GetSearchResponse;
import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClient;
import io.playqd.data.SearchRequestParams;

public class SearchEngineImpl implements SearchEngine {

    private final PlayqdClient playqdClient;

    public SearchEngineImpl() {
        this.playqdClient = PlayqdClient.builder()
                .apiBaseUrl("http://localhost:8016/api/v1")
                .build();
    }

    @Override
    public GetSearchResponse search(SearchRequestParams request, PageRequest pageRequest) {
        return playqdClient.search(request, pageRequest);
    }
}
