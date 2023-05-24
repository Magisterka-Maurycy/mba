package org.maurycy.framework.mba.grpc

import io.quarkus.grpc.GrpcService
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import org.maurycy.framework.mba.generated.AddDataReply
import org.maurycy.framework.mba.generated.AddDataRequest
import org.maurycy.framework.mba.generated.DataService
import org.maurycy.framework.mba.generated.GetAllDataRequest
import org.maurycy.framework.mba.generated.GetDataReply
import org.maurycy.framework.mba.generated.GetDataRequest
import org.maurycy.framework.mba.model.DataDto
import org.maurycy.framework.mba.repository.DataRepository

@GrpcService
class DataServiceGrpcImpl(
    private val dataRepository: DataRepository
) : DataService {
    override fun addData(request: AddDataRequest): Uni<AddDataReply> {
          return dataRepository.persist(DataDto(data = request.dataMap))
            .onItem().transform { persistedDataDto ->
                AddDataReply.newBuilder()
                    .setId(persistedDataDto.id.toString())
                    .build()
            }
    }

    override fun getDataById(request: GetDataRequest): Uni<GetDataReply> {
        return dataRepository.findById(request.id)
            .map { dataToReturn ->
                if (dataToReturn == null) {
                    throw NullPointerException()
                }
                GetDataReply.newBuilder()
                    .setId(dataToReturn.id)
                    .putAllData(dataToReturn.data)
                    .build()
            }
    }

    override fun getAllData(request: GetAllDataRequest?): Multi<GetDataReply> {
        return dataRepository.findAll().stream().map { it: DataDto ->
            GetDataReply.newBuilder()
                .setId(it.id)
                .putAllData(it.data)
                .build()
        }
    }

}