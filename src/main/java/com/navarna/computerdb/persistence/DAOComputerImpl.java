package com.navarna.computerdb.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Optional;

import com.navarna.computerdb.exception.DAOException;
import com.navarna.computerdb.mapper.TransformationResultSet;
import com.navarna.computerdb.model.Computer;
import com.navarna.computerdb.model.Page;


public enum DAOComputerImpl implements DAOComputer {
    INSTANCE;

    public static final String INSERT;
    public static final String UPDATE;
    public static final String DELETE;
    public static final String SELECT_LIST;
    public static final String FIND_ID;
    public static final String FIND_NAME;
    public static final String FIND_COMPANY;
    public static final String COUNT;
    public static final String COUNT_NAME;
    public static final String COUNT_NAME_COMPANY;

    static {
        INSERT = "INSERT INTO computer VALUES ( ?, ?, ?, ?, ? )";
        UPDATE = "UPDATE computer SET name = ?, introduced = ?, discontinued = ?, company_id = ? where id = ?";
        DELETE = "DELETE FROM computer where id = ?";
        SELECT_LIST = "SELECT * from computer left join company on company_id = company.id LIMIT ? OFFSET ?";
        FIND_ID = "SELECT * from computer left join company on company_id = company.id where computer.id = ?";
        FIND_NAME = "SELECT * from computer left join company on company_id = company.id where computer.name = ? LIMIT ? OFFSET ?";
        FIND_COMPANY = "SELECT * from computer left join company on company_id = company.id where company.name = ? LIMIT ? OFFSET ?";
        COUNT = "SELECT count(id) from computer";
        COUNT_NAME = "SELECT count(id) from computer where name = ?";
        COUNT_NAME_COMPANY = "SELECT count(computer.id) from computer left join company on company_id = company.id where company.name = ?";
    }

    public static DAOComputerImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean insert(Computer computer) {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
                PreparedStatement statement = conn.prepareStatement(INSERT)) {
            int result = 0;
            setStatementInsert(statement, computer);
            result = statement.executeUpdate();
            return result != 0 ;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    @Override
    public boolean update(Computer computer) {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
                PreparedStatement statement = conn.prepareStatement(UPDATE)) {
            int result = 0;
            setStatementUpdate(statement, computer);
            result = statement.executeUpdate();
            return result != 0;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
            PreparedStatement statement = conn.prepareStatement(DELETE)) {
            int result = 0;
            statement.setLong(1, id);
            result = statement.executeUpdate();
            return result != 0;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    @Override
    public Page<Computer> list(int numPage, int nbElement) {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
                PreparedStatement statement = conn.prepareStatement(SELECT_LIST)) {
            ResultSet result = null;
            setStatementListe(statement, numPage, nbElement);
            result = statement.executeQuery();
            Page<Computer> page = TransformationResultSet.extraireDetailsComputers(result, numPage, nbElement);
            return page;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    @Override
    public Optional<Computer> findById(long id) {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
                PreparedStatement statement = conn.prepareStatement(FIND_ID)) {
            ResultSet result = null;
            statement.setLong(1, id);
            result = statement.executeQuery();
            Optional<Computer> computer = TransformationResultSet.extraireDetailsComputer(result);
            return computer;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    @Override
    public Page<Computer> findByName(String name, int numPage, int nbElement) {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
                PreparedStatement statement = conn.prepareStatement(FIND_NAME)) {
            ResultSet result = null;
            setStatementShowName(statement, name, numPage, nbElement);
            result = statement.executeQuery();
            Page<Computer> page = TransformationResultSet.extraireDetailsComputers(result, numPage, nbElement);
            return page;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    @Override
    public Page<Computer> findByCompany(String nameCompany, int numPage, int nbElement) {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
                PreparedStatement statement = conn.prepareStatement(FIND_COMPANY)) {
            ResultSet result = null;
            setStatementShowName(statement, nameCompany, numPage, nbElement);
            result = statement.executeQuery();
            Page<Computer> page = TransformationResultSet.extraireDetailsComputers(result, numPage, nbElement);
            return page;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    @Override
    public int count() {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
                Statement statement = conn.createStatement()) {
            ResultSet result = null;
            result = statement.executeQuery(COUNT);
            int retour = TransformationResultSet.extraireNombreElement(result);
            return retour;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    @Override
    public int countWithName(String name) {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
                PreparedStatement statement = conn.prepareStatement(COUNT_NAME)) {
            ResultSet result = null;
            statement.setString(1, name);
            result = statement.executeQuery();
            int retour = TransformationResultSet.extraireNombreElement(result);
            return retour;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    @Override
    public int countWithNameCompany(String nameCompany) {
        try (Connection conn = ConnectionPoolDB.getInstance().open();
                PreparedStatement statement = conn.prepareStatement(COUNT_NAME_COMPANY)) {
            ResultSet result = null;
            statement.setString(1, nameCompany);
            result = statement.executeQuery();
            int retour = TransformationResultSet.extraireNombreElement(result);
            return retour;
        } catch (SQLException se) {
            throw new DAOException("Erreur de base de donnée", se);
        }
    }

    /**
     * Introduit les arguments de la fonction dans le statement(fonction list).
     * @param statement : Preparedstatement en cours
     * @param numPage : numero de page
     * @param nbElement : nombre d'élément
     * @throws SQLException : SQL exception possible
     */
    private void setStatementListe(PreparedStatement statement, int numPage, int nbElement) throws SQLException {
        statement.setInt(1, nbElement);
        statement.setInt(2, numPage * nbElement);
    }

    /**
     * Introduit les arguments de la fonction dans le statement(fonction
     * insert).
     * @param statement : Preparedstatement en cours
     * @param computer : computer à insérer
     * @throws SQLException : SQL exception possible
     */
    private void setStatementInsert(PreparedStatement statement, Computer computer) throws SQLException {
        if (computer.getId() != 0) {
            statement.setLong(1, computer.getId());
        } else {
            statement.setNull(1, Types.BIGINT);
        }
        statement.setString(2, computer.getName());
        if (computer.getIntroduced() != null) {
            statement.setTimestamp(3, Timestamp.valueOf(computer.getIntroduced().atStartOfDay()));
        } else {
            statement.setNull(3, Types.TIMESTAMP);
        }
        if (computer.getDiscontinued() != null) {
            statement.setTimestamp(4, Timestamp.valueOf(computer.getDiscontinued().atStartOfDay()));
        } else {
            statement.setNull(4, Types.TIMESTAMP);
        }
        if (computer.getCompany().getId() != 0) {
            statement.setLong(5, computer.getCompany().getId());
        } else {
            statement.setNull(5, Types.BIGINT);
        }
    }

    /**
     * Introduit les arguments de la fonction dans le statement(fonction
     * update).
     * @param statement : Preparedstatement en cours
     * @param computer : computer à insérer
     * @throws SQLException : SQL exception possible
     */
    private void setStatementUpdate(PreparedStatement statement, Computer computer) throws SQLException {
        if (computer.getId() != 0) {
            statement.setLong(5, computer.getId());
        } else {
            throw new DAOException("Update n'as pas de id arret de la fonction");
        }
        statement.setString(1, computer.getName());
        if (computer.getIntroduced() != null) {
            statement.setTimestamp(2, Timestamp.valueOf(computer.getIntroduced().atStartOfDay()));
        } else {
            statement.setNull(2, Types.TIMESTAMP);
        }
        if (computer.getDiscontinued() != null) {
            statement.setTimestamp(3, Timestamp.valueOf(computer.getDiscontinued().atStartOfDay()));
        } else {
            statement.setNull(3, Types.TIMESTAMP);
        }
        if (computer.getCompany().getId() != 0) {
            statement.setLong(4, computer.getCompany().getId());
        } else {
            statement.setNull(4, Types.BIGINT);
        }

    }

    /**
     * Introduit les arguments de la fonction dans le statement(fonction
     * showName).
     * @param statement : Preparedstatement en cours
     * @param name : nom du computer
     * @param numPage : numero de page
     * @param nbElement : nombre d'élément
     * @throws SQLException : SQL exception possible
     */
    private void setStatementShowName(PreparedStatement statement, String name, int numPage, int nbElement)
            throws SQLException {
        statement.setString(1, name);
        statement.setInt(2, nbElement);
        statement.setInt(3, numPage * nbElement);
    }
}