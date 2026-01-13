package com.nlu.store.core.data.specification;
@FunctionalInterface
public interface Specification<T> {

    Criteria toCriteria();

    static <T> Specification<T> where(Specification<T> spec) {
        return spec == null ? Criteria::of : spec;
    }

    default Specification<T> and(Specification<T> other) {
        return () -> {
            Criteria main = this.toCriteria();
            if (other != null) {
                main.and(other.toCriteria());
            }
            return main;
        };
    }

    default Specification<T> or(Specification<T> other) {
        return () -> {
            Criteria root = Criteria.of();
            root.and(this.toCriteria());
            if (other != null) {
                root.or(other.toCriteria());
            }
            return root;
        };
    }
}

