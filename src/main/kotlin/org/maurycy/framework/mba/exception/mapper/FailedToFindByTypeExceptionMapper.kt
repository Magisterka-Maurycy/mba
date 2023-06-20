package org.maurycy.framework.mba.exception.mapper

import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.maurycy.framework.mba.exception.FailedToFindByTypeException

@Provider
class FailedToFindByTypeExceptionMapper : ExceptionMapper<FailedToFindByTypeException> {
    override fun toResponse(exception: FailedToFindByTypeException?): Response {
        return Response.status(Response.Status.NO_CONTENT).build()
    }
}