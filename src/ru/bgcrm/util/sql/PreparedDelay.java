package ru.bgcrm.util.sql;

import java.io.Closeable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.bgcrm.util.TimeUtils;
import ru.bgcrm.util.Utils;
import ru.bgerp.util.Log;

/**
 * Dynamically building of PreparedStatements with
 * adding params without definition of position index.
 *
 * @author Shamil Vakhitov
 */
public class PreparedDelay implements Closeable {
    private static final Log log = Log.getLog();

    private Connection con;
    private StringBuilder query;

    private List<Object> psSets = new ArrayList<>();
    private PreparedStatement ps;

    private int pos;

    public PreparedDelay(Connection con) {
        this.con = con;
    }

    public PreparedDelay(Connection con, String query) {
        this.con = con;
        addQuery(query);
    }

    public int getPos() {
        return pos;
    }

    public PreparedStatement getPrepared() {
        return ps;
    }

    public StringBuilder getQuery() {
        return query;
    }

    /**
     * Adds SQL string to the query.
     * @param value
     * @return the current object.
     */
    public PreparedDelay addQuery(String value) {
        if (Utils.isBlankString(value)) {
            return this;
        }

        if (query == null) {
            query = new StringBuilder();
        }
        query.append(value);

        return this;
    }

    /**
     * Replace the current query.
     * @param value
     */
    public void setQuery(String value) {
        query.setLength(0);
        query.append(value);

    }

    /**
     * Add int parameter in the prepared statement.
     * @param value
     */
    public void addInt(int value) {
        psSets.add(value);
    }

    /** Add long parameter in the prepared statement
     * @param value
     */
    public void addLong(long value) {
        psSets.add(value);
    }

    /** Add Decimal parameter in the prepared statement.
     * @param value
     */
    public void addBigDecimal(BigDecimal value) {
        psSets.add(value);
    }

    /**
     * Add int parameter in the prepared statement.
     * @param value
     */
    public void addString(String value) {
        psSets.add(value);
    }

    /**
     * Add Date parameter in the prepared statement.
     * @param value
     */
    public void addDate(Date value) {
        psSets.add(TimeUtils.convertDateToSqlDate(value));
    }

    /**
     * Add Timestamp parameter in the prepared statement.
     * @param value
     */
    public void addTimestamp(Timestamp value) {
        psSets.add(value);
    }

    /**
     * Add Boolean parameter in the prepared statement.
     * @param value
     */
    public void addBoolean(Boolean value) {
        psSets.add(value);
    }

    /**
     * Add parameters with obitary types in the prepared statement.
     * @param values
     */
    public void addObjects(Object... values) {
        for (Object value : values)
            psSets.add(value);
    }

    /**
     * Execute the prepared statement for select.
     * @return
     * @throws SQLException
     */
    public ResultSet executeQuery() throws SQLException {
        prepareStatementAndSetParameters();
        return ps.executeQuery();
    }

    /**
     * Execute the prepared statement for update.
     * @return
     * @throws SQLException
     */
    public int executeUpdate() throws SQLException {
        prepareStatementAndSetParameters();
        return ps.executeUpdate();
    }

    private void prepareStatementAndSetParameters() throws SQLException {
        if (ps == null) {
            ps = con.prepareStatement(query.toString());
        }
        setParameters();
    }

    protected void setParameters() throws SQLException {
        final int size = psSets.size();
        for (int i = 0; i < size; i++) {
            ps.setObject(i + 1, psSets.get(i));
        }
    }

    @Override
    public void close() {
        try {
            if (ps != null)
                ps.close();
            ps = null;
            psSets.clear();
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public String toString() {
        return Log.format("Prepared query: %s, params: %s", query, psSets);
    }
}