package com.dockermonitor.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SpringBootTest
public class WebConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void shouldReturnViewResolverBean() {
        final var viewResolver = (ThymeleafViewResolver) applicationContext.getBean("viewResolver");

        assertThat(viewResolver).isNotNull();
        assertThat(viewResolver.getCharacterEncoding()).isEqualTo("UTF-8");
    }

    @Test
    public void shouldReturnTemplateEngineBean() {
        final var templateEngine = applicationContext.getBean(SpringTemplateEngine.class);

        assertThat(templateEngine).isNotNull();
        assertThat(templateEngine.getTemplateResolvers()).hasSize(1);
    }

    @Test
    public void shouldReturnTemplateResolver() {
        final var templateEngine = applicationContext.getBean(SpringTemplateEngine.class);
        final var templateResolver = (ClassLoaderTemplateResolver) templateEngine.getTemplateResolvers().iterator().next();

        assertThat(templateResolver).isNotNull();
        assertThat(templateResolver.getPrefix()).isEqualTo("templates/");
        assertThat(templateResolver.getSuffix()).isEqualTo(".html");
        assertThat(templateResolver.getTemplateMode().name()).isEqualTo("HTML");
        assertThat(templateResolver.getCharacterEncoding()).isEqualTo("UTF-8");
        assertThat(templateResolver.isCacheable()).isFalse();
    }
}