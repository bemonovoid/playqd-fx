package io.playqd.service;

import io.playqd.client.GetSearchResponse;
import io.playqd.client.PageRequest;
import io.playqd.data.SearchRequestParams;

public interface SearchEngine {

    GetSearchResponse search(SearchRequestParams request, PageRequest pageRequest);
}
