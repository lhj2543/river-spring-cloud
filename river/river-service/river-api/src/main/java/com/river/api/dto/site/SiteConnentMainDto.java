package com.river.api.dto.site;

import com.river.api.entity.site.SiteConnent;
import com.river.api.entity.site.SiteConnentMain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author river
 * @date 2020-08-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SiteConnentMainDto extends SiteConnentMain {

    /**
     * 关键字搜索
     */
    private String searchValue;

    /**
     * 内容
     */
    private String bodys;

    /**
     * 内容ID
     */
    private String connectId;

}
