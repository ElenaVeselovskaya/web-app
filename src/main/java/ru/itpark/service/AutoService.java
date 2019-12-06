package ru.itpark.service;

import ru.itpark.domain.Auto;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutoService {
    private final DataSource dataSource;

    public AutoService() throws NamingException, SQLException {
        var context = new InitialContext();
        dataSource = (DataSource) context.lookup("java:/comp/env/jdbc/db");
        try (var connection = dataSource.getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS autos (id TEXT PRIMARY KEY, name TEXT NOT null, description NOT null, image TEXT);");
            }
        }
    }

    public List<Auto> getAll() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            try (var statement = connection.createStatement()) {
                try (var resultSet = statement.executeQuery(" SELECT id, name , description, image FROM autos;")) {
                    var list = new ArrayList<Auto>();
                    while (resultSet.next()) {
                        list.add(new Auto(
                                resultSet.getString("id"),
                                resultSet.getString("name"),
                                resultSet.getString("description"),
                                resultSet.getString("image")));
                    }
                    return list;
                }
            }
        }
    }

    public void create(String name, String description, String image) throws SQLException {
        try (var connection = dataSource.getConnection()) {
            try (var statement = connection.prepareStatement("Insert into autos (id, name, description, image) VALUES (?,?,?,?);")) {
                statement.setString(1, UUID.randomUUID().toString());
                statement.setString(2, name);
                statement.setString(3, description);
                statement.setString(4, image);
                statement.execute();
            }
        }
    }
}


