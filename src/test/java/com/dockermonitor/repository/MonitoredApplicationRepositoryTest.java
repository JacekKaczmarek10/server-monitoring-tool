package com.dockermonitor.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.dockermonitor.entity.MonitoredApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MonitoredApplicationRepositoryTest {

    @Autowired
    private MonitoredApplicationRepository repository;

    private MonitoredApplication app1;
    private MonitoredApplication app2;

    @BeforeEach
    public void setUp() {
        app1 = new MonitoredApplication();
        app1.setName("App1");
        app1.setUrl("https://app1.com");
        app1.setActive(true);

        app2 = new MonitoredApplication();
        app2.setName("App2");
        app2.setUrl("https://app2.com");
        app2.setActive(false);

        repository.save(app1);
        repository.save(app2);
    }

    @Test
    public void shouldSaveMonitoredApplication() {
        final var app = new MonitoredApplication();
        app.setName("App3");
        app.setUrl("https://app3.com");
        app.setActive(true);

        final var savedApp = repository.save(app);

        assertThat(savedApp).isNotNull();
    }

    @Test
    public void shouldFindById() {
        final var foundApp = repository.findById(app1.getId());

        assertThat(foundApp).isPresent();
        assertThat(foundApp.get().getName()).isEqualTo(app1.getName());
    }

    @Test
    public void shouldFindAll() {
        final var apps = repository.findAll();

        assertThat(apps.iterator().hasNext()).isNotNull();
    }

    @Test
    public void shouldDeleteById() {
        repository.deleteById(app1.getId());

        final var foundApp = repository.findById(app1.getId());

        assertThat(foundApp).isNotPresent();
    }

    @Test
    public void shouldUpdateMonitoredApplication() {
        final var foundApp = repository.findById(app2.getId());
        final var app = foundApp.get();
        app.setName("UpdatedApp2");
        repository.save(app);

        final var updatedApp = repository.findById(app2.getId());

        assertThat(foundApp).isPresent();
        assertThat(updatedApp).isPresent();
        assertThat(updatedApp.get().getName()).isEqualTo("UpdatedApp2");
    }
}
