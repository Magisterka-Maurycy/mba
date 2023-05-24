package org.maurycy.framework.mba.exception.mapper

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.maurycy.framework.mba.exception.FailedToFindByIdException

@Provider
class FailedToFindByIdExceptionMapper : ExceptionMapper<FailedToFindByIdException> {
    override fun toResponse(exception: FailedToFindByIdException?): Response {
        return Response.status(Response.Status.NO_CONTENT).build()
    }
}