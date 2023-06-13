package org.maurycy.framework.mba

import io.quarkus.mongodb.panache.kotlin.reactive.ReactivePanacheQuery
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Uni
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.maurycy.framework.mba.model.DataDto
import org.maurycy.framework.mba.generated.AddDataRequest
import org.maurycy.framework.mba.generated.GetAllDataRequest
import org.maurycy.framework.mba.generated.GetDataRequest
import org.maurycy.framework.mba.grpc.DataServiceGrpcImpl
import org.maurycy.framework.mba.repository.DataRepository
import org.mockito.Mockito


@QuarkusTest
class DataServiceGrpcImplTest {


    @Test
    fun addData() {
        val dataService = DataServiceGrpcImpl(dataRepository)
        val addDataRequest = AddDataRequest.newBuilder().putAllData(map1).build()
        dataService.addData(addDataRequest).map { response ->
            Assertions.assertNotNull(response.id)
        }
    }

    @Test
    fun getDataById() {
        val dataService = DataServiceGrpcImpl(dataRepository)
        val getDataRequest = GetDataRequest.newBuilder()
            .setId(hexString)
            .build()
        dataService.getDataById(getDataRequest).map { response ->
            Assertions.assertEquals(hexString, response.id)
            Assertions.assertEquals(map1, response.dataMap)
        }
    }

    @Test
    fun getAllData() {
        val dataService = DataServiceGrpcImpl(dataRepository)
        val getAllDataRequest = GetAllDataRequest.newBuilder()
            .build()
        dataService.getAllData(getAllDataRequest).map { response ->
            Assertions.assertEquals(hexString, response.id)
            Assertions.assertEquals(map1, response.dataMap)
        }
    }

    companion object {
        private val hexString = "63e79d0ae5b643052ff92664"
        private val map1 = mapOf(Pair("1", "a"), Pair("2", "b"), Pair("3", "c"))
        private var dataRepository: DataRepository = Mockito.mock(DataRepository::class.java)

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            val query: ReactivePanacheQuery<DataDto> = Mockito.mock(ReactivePanacheQueryDataDto::class.java)
            Mockito.`when`(dataRepository.findAll()).thenReturn(
                query
            )
            Mockito.`when`(query.list()).thenReturn(
                Uni.createFrom().item(
                    listOf(DataDto(hexString, "typeDef" ,map1))
                )
            )

        }
    }

    private interface ReactivePanacheQueryDataDto : ReactivePanacheQuery<DataDto>
}