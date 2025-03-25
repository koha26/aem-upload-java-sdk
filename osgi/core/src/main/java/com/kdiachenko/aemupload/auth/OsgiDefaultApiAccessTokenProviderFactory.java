package com.kdiachenko.aemupload.auth;

import com.kdia.aemupload.auth.ApiAccessTokenProvider;
import com.kdia.aemupload.auth.ApiAccessTokenProviderFactory;
import com.kdia.aemupload.auth.DefaultApiAccessTokenProviderFactory;
import com.kdia.aemupload.config.ApiAccessTokenConfiguration;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = ApiAccessTokenProviderFactory.class,
        properties = {
                Constants.SERVICE_DESCRIPTION + "=" + "AEM Upload SDK - Default API Access Token Provider Factory",
                Constants.SERVICE_RANKING + "=" + "10"
        }
)
public class OsgiDefaultApiAccessTokenProviderFactory implements ApiAccessTokenProviderFactory {

    private final ApiAccessTokenProviderFactory apiAccessTokenProviderFactory;

    @Activate
    public OsgiDefaultApiAccessTokenProviderFactory(@Reference ApiAccessTokenConfiguration apiAccessTokenConfiguration) {
        apiAccessTokenProviderFactory = new DefaultApiAccessTokenProviderFactory(apiAccessTokenConfiguration);
    }

    @Override
    public ApiAccessTokenProvider create() {
        return apiAccessTokenProviderFactory.create();
    }
}
