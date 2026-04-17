package com.servicemanager.exception;

/**
 * ServiceException.java - STUB VERSION
 * 
 * TASK: Create an abstract base exception class.
 * 
 * What you need to create:
 * ======================
 * 1. ABSTRACT CLASS that extends Exception
 *    - Cannot create ServiceException directly, only subclass it
 * 
 * 2. CONSTRUCTOR - passes message to parent Exception
 * 
 * 3. ABSTRACT METHOD - all subclasses must implement:
 *    - getRecoveryHint() returns String - how to recover from this error
 * 
 * Concepts: INHERITANCE + ABSTRACTION
 * ============================================
 * INHERITANCE: All exceptions in this program extend this class
 * ABSTRACT: Cannot create instance, defines contract for subclasses
 * 
 * Pseudocode:
 * ==========
 * ABSTRACT CLASS ServiceException EXTENDS Exception:
 *     CONSTRUCTOR ServiceException(message):
 *         super(message)
 * 
 *     ABSTRACT METHOD getRecoveryHint() RETURN String
 * 
 * Subclasses to create (later):
 * =========================
 * - ServiceNotFoundException
 * - ServiceNotRunningException
 * - ServiceAlreadyRunningException
 * - ConfigLoadException
 * - InvalidCommandException
 */
public abstract class ServiceException extends Exception {
    
    protected String serviceName;
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }
    
    public abstract String getRecoveryHint();
    
}