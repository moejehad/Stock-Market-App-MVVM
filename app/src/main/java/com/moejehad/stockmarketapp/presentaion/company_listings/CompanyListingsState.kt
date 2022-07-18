package com.moejehad.stockmarketapp.presentaion.company_listings

import com.moejehad.stockmarketapp.domain.model.CompanyListing

data class CompanyListingsState(
    val companies: List<CompanyListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing : Boolean = false,
    val searchQuery : String = ""
)