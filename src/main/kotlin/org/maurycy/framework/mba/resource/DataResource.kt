package org.maurycy.framework.mba.resource

import com.mongodb.MongoWriteException
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.mutiny.replaceWithUnit
import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.ResponseStatus
import org.maurycy.framework.mba.exception.FailedToFindByIdException
import org.maurycy.framework.mba.exception.FailedToPersistDataException
import org.maurycy.framework.mba.model.DataDto
import org.maurycy.framework.mba.model.DataInput
import org.maurycy.framework.mba.repository.DataRepository

@Path("/data")
class DataResource(
    private val dataRepository: DataRepository,
) {



    @GET
    @RolesAllowed("user", "admin")
    fun getAll(): Uni<List<DataDto>> {
        return dataRepository.findAll().list()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseStatus(201)
    @RolesAllowed("user", "admin")
    fun addData(aData: DataDto): Uni<DataDto> {
        try {
            return dataRepository.persist(aData)
// TODO: think about removing catching from this function move it to exception mapper?
        }catch (aE: MongoWriteException){
            throw FailedToPersistDataException()
        }
    }

    @DELETE
    @Path("{id}")
    @ResponseStatus(204)
    @RolesAllowed("user", "admin")
    fun deleteData(@PathParam("id") aId: String): Uni<Unit> {
        return dataRepository.deleteById(aId).replaceWithUnit()
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user", "admin")
    fun putData(@PathParam("id") aId: String, aData: DataInput): Uni<DataDto> {
        return dataRepository.findById(aId).chain { it ->
            if (it == null) {
                throw FailedToFindByIdException(id = aId)
            }
            it.data = aData.data
            it.type = aData.type
            return@chain dataRepository.update(it)
        }

    }

    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user", "admin")
    suspend fun getById(@PathParam("id") aId: String): DataDto {
            return dataRepository.findById(aId).map {
                if (it == null) {
                    throw FailedToFindByIdException(aId)
                }
                return@map it
            }.awaitSuspending()
    }

    @GET
    @Path("type/{type}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user", "admin")
    suspend fun getByType(@PathParam("type") aType: String): List<DataDto> {
        return dataRepository.findByType(aType).map {
            if (it == null) {
                throw FailedToFindByIdException(aType)
            }
            return@map it
        }.awaitSuspending()
    }


}