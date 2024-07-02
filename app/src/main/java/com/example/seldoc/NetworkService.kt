package com.example.seldoc

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList")
    fun getXmlList(
        @Query("serviceKey") apiKey: String,
        @Query("itemName") itemName: String,
        @Query("pageNo") pageNo: Int,
        @Query("startPage") startPage: Int,
        @Query("numOfRows") numOfRows: Int
    ): Call<XmlResponse>

    @GET("/B551182/pharmacyInfoService/getParmacyBasisList")
    fun getPharmacyList(
        @Query("ServiceKey") apiKey: String,
        @Query("yadmNm") yadmNm: String?,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int
    ): Call<PharmacyResponse>
}
