package curb.server.configuration;

import curb.core.CurbDataProvider;
import curb.core.configuration.CurbProperties;
import curb.core.mvc.CurbWebMvcConfigurer;
import curb.core.mvc.interceptor.CurbInterceptor;
import curb.server.api.configuration.CurbApiConfiguration;
import curb.server.interceptor.CurbServerInterceptor;
import curb.server.page.CurbPageConfiguration;
import curb.server.service.AppMenuService;
import curb.server.service.AppService;
import curb.server.service.CurbServerDataProvider;
import curb.server.service.GroupService;
import curb.server.service.PageService;
import curb.server.service.UserPermissionService;
import curb.server.service.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Curb 服务端自动配置入口
 */
@Configuration
@ConditionalOnProperty(name = "curb.enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableConfigurationProperties(CurbServerProperties.class)
@ComponentScan(basePackages = {"curb.server"})
@MapperScan(basePackages = {"curb.server.dao"})
@Import({CurbWebMvcConfigurer.class, CurbApiConfiguration.class, CurbPageConfiguration.class})
public class CurbServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CurbDataProvider.class)
    public CurbServerDataProvider curbServerDataProvider(GroupService groupService,
                                                         AppService appService,
                                                         UserService userService,
                                                         UserPermissionService userPermissionService) {
        return new CurbServerDataProvider(groupService, appService, userService, userPermissionService);
    }

    @Bean
    @ConditionalOnMissingBean(CurbInterceptor.class)
    public CurbServerInterceptor curbServerInterceptor(CurbDataProvider dataProvider,
                                                       CurbServerProperties properties,
                                                       PageService pageService,
                                                       AppService appService,
                                                       AppMenuService appMenuService) {
        return new CurbServerInterceptor(dataProvider, properties);
    }

}
