package com.nlu.store.core.jdbc;

import java.sql.SQLException;

/**
 * Interface used by data access components to process an entire {@link ResultSetReader}
 * in one go, producing a single result object.
 * <p>
 * Unlike {@link RowMapper}, which is called once per row, a {@code ResultSetExtractor}
 * is called only once. The implementation is responsible for iterating through
 * the rows using {@code reader.next()} and managing the state of the result.
 * </p>
 * <p>
 * This is typically used for:
 * <ul>
 *     <li>Building complex object graphs (e.g., a Parent with a List of Children).</li>
 *     <li>Calculating aggregate values manually across multiple rows.</li>
 *     <li>Converting a result set into a specific Map or custom Collection.</li>
 * </ul>
 * </p>
 *
 * @param <T> the type of the result object produced by this extractor.
 * @author Ja Khang
 * @version 1.0
 * @see RowMapper
 */
@FunctionalInterface
public interface ResultSetExtractor<T> {

    /**
     * Processes the provided {@link ResultSetReader} to extract data.
     * <p>
     * Implementations must handle the iteration logic (e.g., {@code while(reader.next())})
     * to traverse the data.
     * </p>
     *
     * @param reader the reader wrapping the result set to extract data from.
     * @return an arbitrary result object, or {@code null} if no data was found.
     * @throws SQLException if a database access error occurs or mapping fails.
     */
    T extractData(ResultSetReader reader) throws SQLException;
}
