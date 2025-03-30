package com.globtrotter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    // Constructors
    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Role(Integer roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    // Getters and Setters
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Add ROLE_ prefix to role name
    public String getRoleWithPrefix() {
        return "ROLE_" + name;
    }

    // toString
    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", name='" + name + '\'' +
                '}';
    }
}
