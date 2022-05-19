package com.xiaomi.data.push.dao.model;

public class Mock {
    private Integer id;

    private String mockKey;

    private String description;

    private Integer version;

    private Integer status;

    private Long created;

    private Long updated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMockKey() {
        return mockKey;
    }

    public void setMockKey(String mockKey) {
        this.mockKey = mockKey == null ? null : mockKey.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }
}