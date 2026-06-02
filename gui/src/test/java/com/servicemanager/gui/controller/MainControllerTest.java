package com.servicemanager.gui.controller;

import com.servicemanager.gui.service.ServiceManager;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class MainControllerTest {

    @Test
    public void setServiceManagerForTestShouldStoreProvidedManager() throws Exception {
        MainController controller = new MainController();
        ServiceManager mockManager = mock(ServiceManager.class);

        controller.setServiceManagerForTest(mockManager);

        Field field = MainController.class.getDeclaredField("serviceManager");
        field.setAccessible(true);

        assertSame(mockManager, field.get(controller));
    }
}