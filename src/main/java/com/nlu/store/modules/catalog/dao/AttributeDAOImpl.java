package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.dao.DataAccessException;
import com.nlu.store.core.dao.JdbcOperations;
import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.dto.AttributeRequest;
import com.nlu.store.modules.catalog.model.Attribute;

import java.time.LocalDateTime;
import java.util.Optional;


public class AttributeDAOImpl implements AttributeDAO {
    // --- SQL Constants ---
    private static final String SELECT_FIELDS = "attribute_id, attribute_created_at, attribute_updated_at, attribute_name, attribute_code, attribute_type, attribute_unit, attribute_is_variant_axis, attribute_is_filterable";
    private static final String SQL_SEARCH_QUERY = "SELECT " + SELECT_FIELDS + " FROM attributes WHERE attribute_name LIKE ? OR attribute_code LIKE ?";
    private static final String SQL_SEARCH_COUNT = "SELECT COUNT(*) FROM attributes WHERE attribute_name LIKE ? OR attribute_code LIKE ?";
    private static final String SQL_FIND_ALL_QUERY = "SELECT " + SELECT_FIELDS + " FROM attributes";
    private static final String SQL_FIND_ALL_COUNT = "SELECT COUNT(*) FROM attributes";
    private static final String SQL_FIND_BY_ID = "SELECT " + SELECT_FIELDS + " FROM attributes WHERE attribute_id = ?";
    private static final String SQL_INSERT = "INSERT INTO attributes (attribute_id, attribute_created_at, attribute_updated_at, attribute_name, attribute_code, attribute_type, attribute_unit, attribute_is_variant_axis, attribute_is_filterable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE attributes SET attribute_updated_at = ?, attribute_name = ?, attribute_code = ?, attribute_type = ?, attribute_unit = ?, attribute_is_variant_axis = ?, attribute_is_filterable = ? WHERE attribute_id = ?";
    private static final String SQL_DELETE = "DELETE FROM attributes WHERE attribute_id = ?";

    // --- Dependencies ---
    private final JdbcOperations jdbcOperations;
    private final AttributeMapper attributeMapper;

    public AttributeDAOImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
        this.attributeMapper = new AttributeMapper();
    }

    @Override
    public Page<Attribute> search(String keyword, Pageable pageable) {
        String pattern = "%" + keyword + "%";
        return jdbcOperations.queryForPage(SQL_SEARCH_QUERY, SQL_SEARCH_COUNT, pageable, attributeMapper, pattern, pattern);
    }

    @Override
    public Page<Attribute> findAll(Pageable pageable) {
        return jdbcOperations.queryForPage(SQL_FIND_ALL_QUERY, SQL_FIND_ALL_COUNT, pageable, attributeMapper);
    }

    @Override
    public Optional<Attribute> findById(ULID id) {
        return jdbcOperations.queryForObject(SQL_FIND_BY_ID, attributeMapper, id.toString());
    }

    @Override
    public ULID create(AttributeRequest req) {
        ULID id = ULID.fast();
        LocalDateTime now = LocalDateTime.now();

        int rows = jdbcOperations.update(SQL_INSERT,
                id.toString(), now, now,
                req.name(), req.code(), req.type().name(), req.unit(), req.isVariantAxis(), req.isFilterable()
        );

        if (rows == 0) throw new DataAccessException("Create failed, no rows affected.");
        return id;
    }

    @Override
    public void update(ULID id, AttributeRequest req) {
        int rows = jdbcOperations.update(SQL_UPDATE,
                LocalDateTime.now(),
                req.name(), req.code(), req.type().name(), req.unit(), req.isVariantAxis(), req.isFilterable(),
                id.toString()
        );

        if (rows == 0) throw new DataAccessException("Update failed, ID not found: " + id);
    }

    @Override
    public void deleteById(ULID id) {
        if (jdbcOperations.update(SQL_DELETE, id.toString()) == 0) {
            throw new DataAccessException("Delete failed, ID not found: " + id);
        }
    }
}
