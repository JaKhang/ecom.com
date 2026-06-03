package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.jdbc.JdbcOperations;
import com.nlu.store.core.data.Sort;
import com.nlu.store.core.data.ULID;
import com.nlu.store.modules.catalog.dao.mappers.SimpleCategoryMapper;
import com.nlu.store.modules.catalog.model.SimpleCategory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryDAOImpl implements CategoryDAO {

    private final JdbcOperations jdbc;
    private final SimpleCategoryMapper mapper = new SimpleCategoryMapper("c_");


    @Inject
    public CategoryDAOImpl(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<SimpleCategory> findAll(Sort sort) {
        return jdbc.queryForList(SELECT_SIMPLE_SQL, sort, mapper);
    }

    @Override
    public Optional<SimpleCategory> findBySlug(String slug) {
        String sql = SELECT_SIMPLE_SQL + "WHERE c.deleted_at IS NULL AND c.slug = ?";
        return jdbc.queryForObject(sql, mapper, slug);
    }

    @Override
    public Optional<SimpleCategory> findById(ULID id) {
        String sql = SELECT_SIMPLE_SQL + " WHERE c.deleted_at IS NULL AND c.id = ?";
        return jdbc.queryForObject(sql, mapper, id);
    }

    @Override
    public List<SimpleCategory> findByProductId(ULID id) {
        String sql = SELECT_SIMPLE_SQL + " LEFT JOIN products_categories pc ON c.id = pc.category_id WHERE c.deleted_at IS NULL AND pc.product_id = ?";
        return jdbc.queryForList(sql, mapper, id);
    }


    private static final String SELECT_SIMPLE_SQL = "SELECT c.id AS c_id, c.created_at AS c_created_at, c.updated_at AS c_updated_at, c.name AS c_name, c.slug AS c_slug, c.icon AS c_icon, c.products_count AS c_products_count FROM categories c ";


}
