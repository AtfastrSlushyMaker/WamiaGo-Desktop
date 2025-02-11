package org.wamiago.wamiago.services;

import java.sql.SQLException;
import java.util.List;

public interface IRide<T> {

    void create(T entity) throws SQLException;

    void update(T entity) throws SQLException;

    void delete(int id) throws SQLException;

    List<T> read() throws SQLException;

    String getClientNameById(int idClient) throws SQLException;

    String getLocationNameById(int idLocation) throws SQLException;
}
