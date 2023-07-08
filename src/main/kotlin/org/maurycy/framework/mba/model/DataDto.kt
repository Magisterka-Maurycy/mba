package org.maurycy.framework.mba.model

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import org.bson.codecs.pojo.annotations.BsonId


@MongoEntity(collection = "StoredData")
data class DataDto(
    @BsonId
    var id: String? = null,
    var type: String? = null,
    var dataStorage: Map<String, String>? = null,
) : PanacheMongoEntityBase()