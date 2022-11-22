package com.xiaomi.youpin.gwdash.bo;

import com.xiaomi.youpin.gwdash.dao.model.ApiInfoExample;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author Xirui Yang (yangxirui)
 * @version 1.0
 * @since 2022/3/4
 */
public class ListApiInfoParam extends ApiInfoParam {

    private List<String> applications;

    private boolean withBlob;

    private Integer pageLimit;

    private Integer pageOffset = 0;

    private List<Long> ids;

    public ApiInfoExample toExample() {
        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria criteria = example.createCriteria();

        example.setLimit(this.pageLimit == null || this.pageLimit <= 0 ? 100 : Math.min(this.pageLimit, 100));
        example.setOffset(this.pageOffset);

        Optional.ofNullable(this.getIds()).ifPresent(ids -> {
            if (!ids.isEmpty()) {
                criteria.andIdIn(ids);
            }
        });
        Optional.ofNullable(this.getApplications()).ifPresent(applications -> {
            if (!applications.isEmpty()) {
                criteria.andApplicationIn(applications);
            }
        });
        if (StringUtils.isNotBlank(this.getName())) {
            criteria.andNameLike(this.getName() + "%");
        }
        if (StringUtils.isNotBlank(this.getUrl())) {
            criteria.andUrlLike(this.getUrl() + "%");
        }
        if (StringUtils.isNotBlank(this.getPath())) {
            criteria.andPathLike(this.getPath() + "%");
        }
        if (this.getAppSrc() != -1) {
            criteria.andAppSrcEqualTo(this.getAppSrc());
        }
        if (this.getApiSrc() != -1) {
            criteria.andApiSrcEqualTo(this.getApiSrc());
        }
        return example;
    }

    public boolean validate() {
        return (this.ids != null && !this.ids.isEmpty()) || (this.applications != null && !this.applications.isEmpty());
    }

    public List<String> getApplications() {
        return applications;
    }

    public void setApplications(List<String> applications) {
        this.applications = applications;
    }

    public boolean isWithBlob() {
        return withBlob;
    }

    public void setWithBlob(boolean withBlob) {
        this.withBlob = withBlob;
    }

    public Integer getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(Integer pageLimit) {
        this.pageLimit = pageLimit;
    }

    public Integer getPageOffset() {
        return pageOffset;
    }

    public void setPageOffset(Integer pageOffset) {
        this.pageOffset = pageOffset;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
