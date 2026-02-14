package io.playqd.service;

import io.playqd.client.GetSearchResponse;
import io.playqd.client.PageRequest;
import io.playqd.client.PlayqdClientProvider;
import io.playqd.data.SearchRequestParams;

public class SearchEngineImpl implements SearchEngine {

    @Override
    public GetSearchResponse search(SearchRequestParams request, PageRequest pageRequest) {
        return PlayqdClientProvider.get().search(request, pageRequest);
    }
}
