package com.servicemanager.gui.dao;

public class SQLiteDAOFactory implements DAOFactory {

    @Override
    public ServiceDAO createServiceDAO() {
        return new SQLiteServiceDAO();
    }

    @Override
    public ExecutionDAO createExecutionDAO() {
        return new SQLiteExecutionDAO();
    }

    @Override
    public OutputDAO createOutputDAO() {
        return new SQLiteOutputDAO();
    }
}
