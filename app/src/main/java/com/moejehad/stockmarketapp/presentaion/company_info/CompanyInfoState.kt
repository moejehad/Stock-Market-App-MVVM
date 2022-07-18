package com.moejehad.stockmarketapp.presentaion.company_info

import com.moejehad.stockmarketapp.domain.model.CompanyInfo
import com.moejehad.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val stockInfos : List<IntradayInfo> = emptyList(),
    val company : CompanyInfo? = null,
    val isLoading : Boolean? = null,
    val error : String? = null
)