package com.river.site.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.river.api.entity.site.QuartzJobDetail;
import com.river.site.mapper.QuartzJobDetailMapper;
import com.river.site.service.IQuartzJobDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * quartz job detail 服务实现类
 * </p>
 *
 * @author river
 * @since 2020-10-04
 */
@Service
public class QuartzJobDetailServiceImpl extends ServiceImpl<QuartzJobDetailMapper, QuartzJobDetail> implements IQuartzJobDetailService {

}
