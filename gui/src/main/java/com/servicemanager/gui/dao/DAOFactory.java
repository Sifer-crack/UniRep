package com.servicemanager.gui.dao;

public interface DAOFactory {

    ServiceDAO createServiceDAO();

    ExecutionDAO createExecutionDAO();

    OutputDAO createOutputDAO();
}
