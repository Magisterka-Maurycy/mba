package org.maurycy.framework.mba.exception.mapper

import com.mongodb.MongoWriteException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class MongoWriteExceptionMapper: ExceptionMapper<MongoWriteException> {
    override fun toResponse(exception: MongoWriteException?): Response {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()
    }
}