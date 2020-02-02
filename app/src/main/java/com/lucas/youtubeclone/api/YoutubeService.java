package com.lucas.youtubeclone.api;

import com.lucas.youtubeclone.model.Resultado;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface YoutubeService {

    /**
     * https://www.googleapis.com/youtube/v3/
     * search
     * ?part=snippet
     * &order=date
     * &maxResults=20
     * &key= chave da api
     * &channel= chave do canal
     * &q= pesquisa (separar espaço po +)
     */

    @GET("search")
    Observable<Resultado> recuperarVideos(@Query("part") String part,
                                          @Query("order") String order,
                                          @Query("maxResults") String maxResults,
                                          @Query("key") String key,
                                          @Query("channelId") String channelId,
                                          @Query("q") String q);
}
