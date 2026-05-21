package com.servicemanager.gui.dao;

/** Concrete factory that creates SQLite-based DAO implementations.
 *  To switch to a different database (e.g. Derby), implement
 *  a new DAOFactory and wire it in ServiceManager's constructor. */
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
