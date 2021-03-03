package com.river.site.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.river.api.dto.site.SiteConnentMainDto;
import com.river.api.entity.site.SiteConnentMain;

/**
 * <p>
 * 站点内容主表 Mapper 接口
 * </p>
 *
 * @author river
 * @since 2020-09-17
 */
public interface SiteConnentMainMapper extends BaseMapper<SiteConnentMain> {

    /**
     * 内容详情
     * @return
     */
    SiteConnentMainDto getDetail(String sid);

    /**
     * 内容删除
     * @return
     */
    boolean relationDelete(String sid);

}
