package org.wamiago.wamiago.services;


import java.sql.SQLException;
import java.util.List;

public interface IService<T> {

    void createReclamation(T t) throws SQLException;
    void updateReclamation(T t)throws SQLException;
    void deleteReclamation(int id)throws SQLException;
    List<T> read() throws SQLException;
}
