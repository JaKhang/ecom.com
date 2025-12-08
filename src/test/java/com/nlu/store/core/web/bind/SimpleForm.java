package com.nlu.store.core.web.bind;

import java.util.List;
import java.util.Set;

public class SimpleForm {
    private String username;
    private int age;
    private List<Integer> ids;
    private Set<String> roles;
    private String[] tags;

    // Setter để kiểm tra ReflectionUtils ưu tiên Setter
    public void setUsername(String username) {
        this.username = "SETTER_" + username;
    }

    // Getters
    public String getUsername() { return username; }
    public int getAge() { return age; }
    public List<Integer> getIds() { return ids; }
    public Set<String> getRoles() { return roles; }
    public String[] getTags() { return tags; }


}