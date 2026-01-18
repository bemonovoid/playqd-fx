package io.playqd.service;

import io.playqd.client.GetAlbumsResponse;
import io.playqd.client.GetArtistsResponse;
import io.playqd.client.GetGenresResponse;
import io.playqd.client.GetTracksResponse;

public record SearchResponse(GetTracksResponse tracksResponse,
                             GetArtistsResponse artistsResponse,
                             GetAlbumsResponse albumsResponse,
                             GetGenresResponse getGenresResponse) {


}
