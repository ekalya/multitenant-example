package com.multitenant.example.multitenantexample;


import com.multitenant.example.multitenantexample.TenantContext;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author exk
 */

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
    @Autowired
    private DataSource dataSource;
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }
    @Override
    public Connection getConnection(String tenantIdentifie) throws SQLException {
        String tenantIdentifier = TenantContext.getCurrentTenant();
        final Connection connection = getAnyConnection();
        try {
            if (tenantIdentifier != null) {
                connection.createStatement().execute("USE " + tenantIdentifier);
            } else {
                connection.createStatement().execute("USE public");
            }
        }
        catch ( SQLException e ) {
            throw new HibernateException(
                    "Problem setting schema to " + tenantIdentifier,
                    e
            );
        }
        return connection;
    }
    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            connection.createStatement().execute( "USE public");
        }
        catch ( SQLException e ) {
            throw new HibernateException(
                    "Problem setting schema to " + tenantIdentifier,
                    e
            );
        }
        connection.close();
    }
    @SuppressWarnings("rawtypes")
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }
    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }
}
