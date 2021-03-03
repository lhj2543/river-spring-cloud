package com.river.site.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.dto.site.SiteConnentMainDto;
import com.river.api.entity.site.SiteConnentMain;
import com.river.site.mapper.SiteConnentMainMapper;
import com.river.site.service.ISiteConnentMainService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 站点内容主表 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-09-17
 */
@Service
public class SiteConnentMainServiceImpl extends ServiceImpl<SiteConnentMainMapper, SiteConnentMain> implements ISiteConnentMainService {


    @Override
    public SiteConnentMainDto getDetail(String sid) {
        return baseMapper.getDetail(sid);
    }

    @Override
    public boolean relationDelete(String sid) {
        return baseMapper.relationDelete(sid);
    }
}
