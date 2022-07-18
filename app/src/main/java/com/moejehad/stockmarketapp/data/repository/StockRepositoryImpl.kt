package com.moejehad.stockmarketapp.data.repository

import com.moejehad.stockmarketapp.data.local.StockDatabase
import com.moejehad.stockmarketapp.data.mapper.toCompanyListing
import com.moejehad.stockmarketapp.data.remote.StockApi
import com.moejehad.stockmarketapp.domain.model.CompanyListing
import com.moejehad.stockmarketapp.domain.repository.StockRepository
import com.moejehad.stockmarketapp.util.Resource
import com.moejehad.stockmarketapp.data.csv.CSVParser
import com.moejehad.stockmarketapp.data.mapper.toCompanyInfo
import com.moejehad.stockmarketapp.data.mapper.toCompanyListingEntity
import com.moejehad.stockmarketapp.domain.model.CompanyInfo
import com.moejehad.stockmarketapp.domain.model.IntradayInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListings = try {
                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListings()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )
                emit(Resource.Success(
                    data = dao.searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }


    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val respnse = api.getIntradayInfo(symbol)
            val result = intradayInfoParser.parse(respnse.byteStream())
            Resource.Success(result)
        }catch (e: IOException) {
            e.printStackTrace()
            Resource.Error("Couldn't load intraday info")
        }catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error("Couldn't load intraday info")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        }catch (e: IOException) {
            e.printStackTrace()
            Resource.Error("Couldn't load Company info")
        }catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error("Couldn't load Company info")
        }
    }
}