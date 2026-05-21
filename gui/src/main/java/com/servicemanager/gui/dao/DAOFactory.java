package com.servicemanager.gui.dao;

/** Abstract Factory for creating DAO instances.
 *  Allows swapping between database backends (SQLite, Derby, etc.)
 *  without changing any business logic code.
 *  Add a new DAOFactory implementation to switch databases. */
public interface DAOFactory {

    ServiceDAO createServiceDAO();

    ExecutionDAO createExecutionDAO();

    OutputDAO createOutputDAO();
}
