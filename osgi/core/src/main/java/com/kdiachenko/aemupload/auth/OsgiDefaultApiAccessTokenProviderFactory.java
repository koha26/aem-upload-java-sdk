package com.kdiachenko.aemupload.auth;

import com.kdia.aemupload.auth.ApiAccessTokenProvider;
import com.kdia.aemupload.auth.ApiAccessTokenProviderFactory;
import com.kdia.aemupload.auth.DefaultApiAccessTokenProviderFactory;
import com.kdia.aemupload.config.ApiAccessTokenConfiguration;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = ApiAccessTokenProviderFactory.class,
        properties = {
                Constants.SERVICE_DESCRIPTION + "=" + "AEM Upload SDK - Default API Access Token Provider Factory"
        }
)
public class OsgiDefaultApiAccessTokenProviderFactory implements ApiAccessTokenProviderFactory {

    private final ApiAccessTokenProviderFactory apiAccessTokenProviderFactory;

    public OsgiDefaultApiAccessTokenProviderFactory(@Reference ApiAccessTokenConfiguration apiAccessTokenConfiguration) {
        apiAccessTokenProviderFactory = new DefaultApiAccessTokenProviderFactory(apiAccessTokenConfiguration);
    }

    @Override
    public ApiAccessTokenProvider create() {
        return apiAccessTokenProviderFactory.create();
    }
}
