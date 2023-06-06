package org.maurycy.framework.mba

import io.quarkus.security.spi.runtime.AuthorizationController
import jakarta.annotation.Priority

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Alternative
import jakarta.interceptor.Interceptor
import org.eclipse.microprofile.config.inject.ConfigProperty


@Alternative
@Priority(Interceptor.Priority.LIBRARY_AFTER)
@ApplicationScoped
class DisabledAuthController : AuthorizationController() {
    @ConfigProperty(name = "disable.authorization", defaultValue = "false")
    var disableAuthorization = false
    override fun isAuthorizationEnabled(): Boolean {
        return !disableAuthorization
    }
}