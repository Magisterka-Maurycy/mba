package org.maurycy.framework.mba

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.quarkus.test.junit.QuarkusTest
import java.util.concurrent.TimeUnit
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.maurycy.framework.mba.generated.AddDataRequest
import org.maurycy.framework.mba.generated.GetAllDataRequest
import org.maurycy.framework.mba.generated.GetDataRequest
import org.maurycy.framework.mba.generated.MutinyDataServiceGrpc


@QuarkusTest
class DataServiceGrpcClientTest {


    @Test
    fun addData() {
        val addDataRequest = AddDataRequest.newBuilder().putData("1", "a")
            .putData("2", "b")
            .putData("3", "c")
            .build()
        dataService!!.addData(addDataRequest).map { reply ->
            try {
                ObjectId(reply.id)
            } catch (aE: IllegalArgumentException) {
                fail(aE)
            }
        }
    }

    @Test
    fun getDataById() {
        val data = mapOf(Pair("1", "a"), Pair("2", "b"), Pair("3", "c"))
        val addDataRequest = AddDataRequest.newBuilder().putAllData(data).build()

        dataService!!.addData(addDataRequest).invoke { created ->
            val getDataRequest = GetDataRequest.newBuilder()
                .setId(created.id)
                .build()
            dataService!!
                .getDataById(getDataRequest).invoke { response ->
                    assert(response.id == created.id)
                    assert(response.dataMap == data)
                }
        }
    }

    @Test
    fun getAllData() {
        val data = mapOf(Pair("1", "a"), Pair("2", "b"), Pair("3", "c"))
        val addDataRequest = AddDataRequest.newBuilder().putAllData(data).build()
        val getAllDataRequest = GetAllDataRequest.newBuilder().build()
        dataService!!.addData(addDataRequest)
            .invoke { created ->
                dataService!!.getAllData(getAllDataRequest).invoke { reply ->
                    assert(reply.id == created.id)
                    assert(reply.dataMap == data)
                }
            }
    }

    companion object {
        private var channel: ManagedChannel? = null
        private var dataService: MutinyDataServiceGrpc.MutinyDataServiceStub? = null

        @JvmStatic
        @AfterAll
        @Throws(InterruptedException::class)
        fun cleanup() {
            channel!!.shutdown()
            channel!!.awaitTermination(10, TimeUnit.SECONDS)
        }

        @JvmStatic
        @BeforeAll
        fun init() {
            channel = ManagedChannelBuilder.forAddress("localhost", 9001).usePlaintext().build()
            dataService = MutinyDataServiceGrpc.newMutinyStub(channel)
        }
    }
}