package com.example.catapi.data

import retrofit2.http.GET

interface CatService {
    @GET("images/search")
    suspend fun getSupportedLanguages(): CatDto
}
