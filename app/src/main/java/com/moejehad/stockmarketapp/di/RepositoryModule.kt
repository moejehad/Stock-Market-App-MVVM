package com.moejehad.stockmarketapp.di

import com.moejehad.stockmarketapp.data.csv.CSVParser
import com.moejehad.stockmarketapp.data.csv.CompanyListingParser
import com.moejehad.stockmarketapp.data.repository.StockRepositoryImpl
import com.moejehad.stockmarketapp.domain.model.CompanyListing
import com.moejehad.stockmarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingParser(
        companyListingParser: CompanyListingParser
    ) : CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ) : StockRepository
}