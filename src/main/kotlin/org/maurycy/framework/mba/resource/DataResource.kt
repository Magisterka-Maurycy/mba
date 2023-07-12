package org.maurycy.framework.mba.resource

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
import org.maurycy.framework.mba.exception.FailedToFindByTypeException
import org.maurycy.framework.mba.model.DataDto
import org.maurycy.framework.mba.model.DataUpdate
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
        return dataRepository.persist(aData)
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
    fun putData(@PathParam("id") aId: String, aData: DataUpdate): Uni<DataDto> {
        return dataRepository.findById(aId).chain { dataToUpdate ->
            if (dataToUpdate == null) {
                throw FailedToFindByIdException(id = aId)
            }
            dataToUpdate.dataStorage = aData.dataStorage
            dataToUpdate.type = aData.type
            return@chain dataRepository.update(dataToUpdate)
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
        val result = dataRepository.findByType(aType).awaitSuspending()
        if (result.isEmpty()) {
            throw FailedToFindByTypeException(aType)
        }
        return result
    }

}