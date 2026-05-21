package com.servicemanager.gui.dao;

import com.servicemanager.gui.model.Service;
import java.sql.SQLException;
import java.util.List;

public interface ServiceDAO {

    void insert(Service service) throws SQLException;

    Service findByName(String name) throws SQLException;

    List<Service> findAll() throws SQLException;

    void update(Service service) throws SQLException;

    void delete(String name) throws SQLException;
}
