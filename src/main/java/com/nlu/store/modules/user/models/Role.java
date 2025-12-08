package com.nlu.store.modules.user.models;
import com.nlu.store.core.data.AbstractModel;
import com.nlu.store.core.data.ULID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
    public class Role extends AbstractModel implements Serializable {

        private String name;
        private String code;

        @Builder
        public Role(ULID id, LocalDateTime createdAt, LocalDateTime updatedAt,
                    String name, String code) {
            super(id, createdAt, updatedAt);
            this.name = name;
            this.code = code;
        }
    }

