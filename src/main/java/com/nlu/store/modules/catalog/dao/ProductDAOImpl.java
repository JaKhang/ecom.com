package com.nlu.store.modules.catalog.dao;

import com.nlu.store.core.data.Page;
import com.nlu.store.core.data.Pageable;
import com.nlu.store.core.data.ULID;
import com.nlu.store.core.jdbc.JdbcOperations;
import com.nlu.store.core.jdbc.sql.WhereBuilder;
import com.nlu.store.modules.catalog.dao.mappers.*;
import com.nlu.store.modules.catalog.dto.ProductFilter;
import com.nlu.store.modules.catalog.model.SimpleProduct;
import com.nlu.store.modules.catalog.model.SimpleVariant;
import com.nlu.store.modules.catalog.model.details.ProductAttribute;
import com.nlu.store.modules.catalog.model.details.ProductImage;
import com.nlu.store.modules.catalog.model.details.ProductSpecs;
import com.nlu.store.modules.catalog.model.details.ProductVariant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductDAOImpl implements ProductDAO {

    /*------------------
            Bean
    --------------------*/
    private final JdbcOperations jdbc;

    /*------------------

    --------------------*/
    private final SimpleProductMapper simpleProductMapper = new SimpleProductMapper("p_", "b_");

    @Inject
    public ProductDAOImpl(JdbcOperations jdbc) {
        this.jdbc = jdbc;
    }


    @Override
    public Page<SimpleProduct> findAll(Pageable pageable) {
        String querySql = SELECT_SIMPLE_PRODUCT_SQL + " WHERE p.deleted_at IS NULL";
        String countSql = "SELECT COUNT(*) FROM products p WHERE p.deleted_at IS NULL";
        return jdbc.queryForPage(querySql, countSql, pageable, simpleProductMapper);
    }

    @Override
    public List<ProductVariant> findVariantsByProductId(ULID productId) {
        String sql = SELECT_PRODUCT_VARIANT_SQL + "  WHERE pv.deleted_at IS NULL AND pv.product_id = ?";

        return jdbc.executeQuery(sql, new ProductVariantsExtractor(), productId).orElse(Collections.emptyList());
    }

    @Override
    public List<ProductAttribute> findAttributesByProductId(ULID productId) {

        return jdbc.executeQuery(SELECT_PRODUCT_ATT_SQL, new ProductAttributesExtractor(), productId).orElse(Collections.emptyList());
    }

    @Override
    public Optional<SimpleProduct> findBySlug(String slug) {
        String querySql = SELECT_SIMPLE_PRODUCT_SQL + " WHERE p.deleted_at IS NULL AND p.slug = ?";
        return jdbc.queryForObject(querySql, simpleProductMapper, slug);
    }

    @Override
    public Optional<SimpleProduct> findById(ULID productId) {
        String querySql = SELECT_SIMPLE_PRODUCT_SQL + " WHERE p.deleted_at IS NULL AND p.id = ?";
        return jdbc.queryForObject(querySql, simpleProductMapper, productId);
    }

    @Override
    public List<ProductImage> findProductImage(ULID productId) {
        return jdbc.executeQuery(SELECT_PRODUCT_IMAGE + " WHERE pi.product_id = ? ORDER BY pi.sort_order", new ProductImagesExtractor(), productId).orElse(Collections.emptyList());
    }


    @Override
    public Page<SimpleProduct> search(String keyword, ULID catsId, Pageable pageable) {
        StringBuilder sql = new StringBuilder(SELECT_SIMPLE_PRODUCT_SQL);
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM products p");

        List<Object> args = new ArrayList<>();

        // 1. Xử lý JOIN (Chỉ join nếu cần lọc theo category)
        if (catsId != null) {
            String joinClause = " JOIN products_categories pc ON p.id = pc.product_id";
            sql.append(joinClause);
            countSql.append(joinClause);
        }

        // 2. Bắt đầu mệnh đề WHERE
        sql.append(" WHERE p.deleted_at IS NULL");
        countSql.append(" WHERE p.deleted_at IS NULL");

        // 3. Điều kiện Category
        if (catsId != null) {
            sql.append(" AND pc.category_id = ?");
            countSql.append(" AND pc.category_id = ?");
            args.add(catsId.toString());
        }

        // 4. Điều kiện Keyword (Full-text search)
        if (keyword != null && !keyword.isBlank()) {
            String matchClause = " AND MATCH(p.name, p.keywords) AGAINST(? IN BOOLEAN MODE)";
            sql.append(matchClause);
            countSql.append(matchClause);
            args.add(prepareSearchQuery(keyword));
        }

        // 5. Thực thi
        return jdbc.queryForPage(
                sql.toString(),
                countSql.toString(),
                pageable,
                simpleProductMapper,
                args.toArray()
        );
    }

    @Override
    public Page<SimpleProduct> search(String keyword, ProductFilter filter, Pageable pageable) {
        StringBuilder sql = new StringBuilder(SELECT_SIMPLE_PRODUCT_SQL);
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM products p");

        List<Object> args = new ArrayList<>();

        // --- 1. Xử lý JOIN ---
        if (filter != null && filter.categoryId() != null) {
            String catJoin = " JOIN products_categories pc ON p.id = pc.product_id";
            sql.append(catJoin);
            countSql.append(catJoin);
        }

        // --- 2. Mệnh đề WHERE cơ sở ---
        sql.append(" WHERE p.deleted_at IS NULL");
        countSql.append(" WHERE p.deleted_at IS NULL");

        // --- 3. Xử lý Keyword (Full-text search) ---
        if (keyword != null && !keyword.isBlank()) {
            String matchClause = " AND MATCH(p.name, p.keywords) AGAINST(? IN BOOLEAN MODE)";
            sql.append(matchClause);
            countSql.append(matchClause);
            args.add(prepareSearchQuery(keyword));
        }

        // --- 4. Xử lý Filter ---
        if (filter != null) {
            // A. Category ID
            if (filter.categoryId() != null) {
                String catClause = " AND pc.category_id = ?";
                sql.append(catClause);
                countSql.append(catClause);
                args.add(filter.categoryId().toString());
            }

            if (filter.brandId() != null && !filter.brandId().isEmpty()) {
                String placeholders = filter.brandId().stream()
                        .map(id -> "?")
                        .collect(Collectors.joining(", "));

                String brandClause = " AND p.brand_id IN (" + placeholders + ")";
                sql.append(brandClause);
                countSql.append(brandClause);

                // Thêm từng ID vào danh sách tham số
                filter.brandId().forEach(id -> args.add(id.toString()));
            }

            if (filter.lowPrice() != null) {
                String minPriceClause = " AND p.min_price >= ?";
                sql.append(minPriceClause);
                countSql.append(minPriceClause);
                args.add(filter.lowPrice());
            }

            if (filter.highPrice() != null) {
                String maxPriceClause = " AND p.min_price <= ?";
                sql.append(maxPriceClause);
                countSql.append(maxPriceClause);
                args.add(filter.highPrice());
            }
        }


        return jdbc.queryForPage(
                sql.toString(),
                countSql.toString(),
                pageable,
                simpleProductMapper,
                args.toArray()
        );
    }


    @Override
    public List<ProductSpecs> findProductSpecs(ULID productId) {
        return jdbc.queryForList(SELECT_PRODUCT_SPECS, new ProductSpecsMapper("ps_"), productId);
    }

    @Override
    public Optional<SimpleVariant> findDefaultVariantForCart(ULID productId) {
        String sql = SELECT_SIMPLE_VARIANT + " WHERE p.id = ? ORDER BY is_default DESC LIMIT 1";
        return jdbc.queryForObject(sql, new SimpleVariantMapper(), productId);
    }

    @Override
    public Optional<SimpleVariant> findSimpleVariantById(ULID variantId) {
        String sql = SELECT_SIMPLE_VARIANT + " WHERE pv.id = ? ";
        return jdbc.queryForObject(sql, new SimpleVariantMapper(), variantId);

    }

    @Override
    public List<ProductVariant> findVariantsByIds(Collection<ULID> variantIds) {
        WhereBuilder sql = WhereBuilder.create(SELECT_PRODUCT_VARIANT_SQL)
                .and("pv.deleted_at IS NULL")
                .andIn("pv.id", variantIds);

        return jdbc.executeQuery(sql.getSql(), new ProductVariantsExtractor(), sql.getParams()).orElse(Collections.emptyList());
    }

    @Override
    public List<SimpleVariant> findSimpleVariantsByIds(Collection<ULID> variantIds) {

        WhereBuilder sql = WhereBuilder.create(SELECT_SIMPLE_VARIANT)
                .and("pv.deleted_at IS NULL")
                .andIn("pv.id", variantIds);
        System.out.println(Arrays.toString(sql.getParams()));
        System.out.println(sql.getSql());
        return jdbc.queryForList(sql.getSql(), new SimpleVariantMapper(), sql.getParams());
    }



    private String prepareSearchQuery(String keyword) {


        if (keyword == null || keyword.isBlank()) return "";

        return Arrays.stream(keyword.trim().split("\\s+"))
                .map(word -> word + "*")
                .collect(Collectors.joining(" "));
    }

    /*------------------
           SQL
    --------------------*/
    private static final String SELECT_SIMPLE_PRODUCT_SQL = "SELECT p.id AS p_id, p.name AS p_name, p.slug AS p_slug, p.thumbnail AS p_thumbnail, p.brand_id AS p_brand_id, p.description AS p_description, p.short_description AS p_short_description, p.status AS p_status, p.is_featured AS p_is_featured, p.specs_snapshot AS p_specs_snapshot, p.min_price AS p_min_price, p.max_price AS p_max_price, p.rating_avg AS p_rating_avg, p.reviews_count AS p_reviews_count, p.created_at AS p_created_at, p.updated_at AS p_updated_at, b.id AS b_id, b.name AS b_name, b.slug AS b_slug, b.logo AS b_logo FROM products p LEFT JOIN brands b ON p.brand_id = b.id";
    private static final String SELECT_PRODUCT_VARIANT_SQL = "SELECT pv.id AS var_id, pv.name AS var_name, pv.is_default AS var_is_default, pv.original_price AS var_original_price, pv.sku AS var_sku, pv.stock_quantity AS var_stock_quantity, pv.price AS var_price, pv.weight_g AS var_weight_g, pv.created_at AS var_created_at, pv.updated_at AS var_updated_at, att.id AS att_id, att.name AS att_label, ao.id AS opt_id, ao.value AS opt_value FROM product_variants pv LEFT JOIN variant_attribute_value vav ON pv.id = vav.variant_id LEFT JOIN attributes att ON vav.attribute_id = att.id LEFT JOIN attribute_values ao ON vav.value_option_id = ao.id";
    private static final String SELECT_PRODUCT_ATT_SQL = "SELECT DISTINCT att.id AS att_id, po.visual_type AS att_visual_type, att.code AS att_code, po.position AS att_position, att.name AS att_label, ao.id AS opt_id, ao.value AS opt_label, COALESCE(pos.type, 'color') AS opt_visual_type, COALESCE(pos.value, ao.visual_value) AS opt_visual_value FROM product_attribute_definitions po JOIN attributes att ON po.attribute_id = att.id JOIN attribute_values ao ON att.id = ao.attribute_id LEFT JOIN product_value_swatches pos ON pos.product_id = po.product_id AND pos.option_id = ao.id INNER JOIN variant_attribute_value vav ON ao.id = vav.value_option_id AND att.id = vav.attribute_id INNER JOIN product_variants pv ON pv.id = vav.variant_id AND pv.product_id = po.product_id WHERE po.product_id = ? ORDER BY po.position";
    private static final String SELECT_PRODUCT_IMAGE = "SELECT pi.id AS img_id, pi.alt_text AS img_alt_text, pi.url AS img_url, pvi.attribute_value_id AS attribute_value_id FROM product_images pi LEFT JOIN product_value_images pvi ON pi.id = pvi.image_id";
    private static final String SELECT_PRODUCT_SPECS = "SELECT ps.id AS ps_id, a.name AS ps_attribute_label, COALESCE(av.value, ps.raw_value) AS ps_value, ps.unit AS ps_unit, ps.is_highlight AS ps_is_highlight FROM product_specs ps JOIN attributes a ON ps.attribute_id = a.id LEFT JOIN attribute_values av ON ps.value_option_id = av.id WHERE ps.product_id = ? ORDER BY ps.is_highlight DESC, a.name ASC;";
    private static final String SELECT_SIMPLE_VARIANT = "SELECT pv.name AS v_name, pv.price AS v_price, pv.sku AS v_sku ,p.rating_avg AS v_rating_avg, p.reviews_count AS v_reviews_count, COALESCE(pv.thumbnail, p.thumbnail) AS v_thumbnail, pv.original_price AS v_original_price, p.id AS v_product_id, pv.id AS v_id, pv.stock_quantity AS v_stocks, p.slug AS v_product_slug FROM product_variants pv LEFT JOIN products p ON pv.product_id = p.id";
}
