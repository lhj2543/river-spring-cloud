package com.river.site.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.river.api.dto.site.SiteConnentMainDto;
import com.river.api.entity.site.SiteConnentMain;

/**
 * <p>
 * 站点内容主表 服务类
 * </p>
 *
 * @author river
 * @since 2020-09-17
 */
public interface ISiteConnentMainService extends IService<SiteConnentMain> {

    SiteConnentMainDto getDetail(String sid);

    boolean relationDelete(String sid);

}
