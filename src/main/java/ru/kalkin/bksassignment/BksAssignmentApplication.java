package ru.kalkin.bksassignment;

import net.sf.ehcache.Ehcache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.IEXCloudTokenBuilder;
import pl.zankowski.iextrading4j.client.IEXTradingApiVersion;
import pl.zankowski.iextrading4j.client.IEXTradingClient;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@PropertySource("classpath:credentials.properties")
public class BksAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BksAssignmentApplication.class, args);
    }

    @Bean
    public IEXCloudClient getCloudClient(
            @Value("${iexcloud.user.publishableToken}") String publishableToken
    ) {
        return IEXTradingClient.create(IEXTradingApiVersion.IEX_CLOUD_V1,
                new IEXCloudTokenBuilder()
                        .withPublishableToken(publishableToken)
                        .build());
    }

    @Bean("validationSource")
    public ResourceBundleMessageSource getMessageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
