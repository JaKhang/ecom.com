package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.jdbc.JdbcOperations;
import com.nlu.store.core.data.Sort;
import com.nlu.store.modules.catalog.dao.mappers.BrandMapper;
import com.nlu.store.modules.catalog.model.Brand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BrandDAOImpl implements BrandDAO {

    private final JdbcOperations jdbc;
    private final BrandMapper mapper = new BrandMapper("b_");

    @Inject
    public BrandDAOImpl(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Brand> findAll(Sort sortOrder) {
        return jdbc.queryForList(SELECT_SQL, sortOrder, mapper);
    }

    @Override
    public Optional<Brand> findBySlug(String slug) {
        String sql = SELECT_SQL + " AND b.slug = ?";
        return jdbc.queryForObject(sql, mapper, slug);
    }

    // Cột number_of_products đã đổi thành products_count
    private static final String SELECT_SQL = "SELECT b.id AS b_id, b.created_at AS b_created_at, b.updated_at AS b_updated_at, b.deleted_at AS b_deleted_at, b.name AS b_name, b.slug AS b_slug, b.logo AS b_logo, b.is_active AS b_is_active, b.products_count AS b_products_count FROM brands b WHERE b.deleted_at IS NULL";
}
