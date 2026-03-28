package br.com.wirizada.smartcondo_api.core.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        return tenantId != null ? tenantId : "DEFAULT_TENANT";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
