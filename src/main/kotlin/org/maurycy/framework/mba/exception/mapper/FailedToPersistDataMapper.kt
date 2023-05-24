package org.maurycy.framework.mba.exception.mapper

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.maurycy.framework.mba.exception.FailedToPersistDataException

@Provider
class FailedToPersistDataMapper : ExceptionMapper<FailedToPersistDataException> {
    override fun toResponse(exception: FailedToPersistDataException?): Response {
        return Response.status(Response.Status.CONFLICT).build()

    }
}