package com.river.site.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.river.common.core.util.Result;
import com.river.common.mybatis.model.DynamicTable;
import com.river.site.service.impl.DynameicService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 站点共同controller
 *
 * @author river
 * @since 2020-09-17
 */
@RestController
@RequestMapping("/siteCommon")
@Slf4j
@Api(value = "siteCommon",tags = "站点共同controller")
public class SiteCommonController {

    @Autowired
    private DynameicService dynameicService;

    @PostMapping("/getMaxNumber")
    public Result getMaxNumber(@RequestBody Map<String,Object> param){

        try {

            if(param!=null && param.size()>0 && param.get("tableName")!=null && param.get("numberColumn")!=null){
                String tableName = (String) param.get("tableName");
                String numberColumn = (String) param.get("numberColumn");

                QueryWrapper query = Wrappers.query();

                if(param.get("wheres")!=null){
                    List<Map<String,String>> wheres = (List<Map<String, String>>) param.get("wheres");
                    wheres.forEach(row->{
                        String column = row.get("column");
                        String opt =  row.get("opt");
                        String value = row.get("value");
                        if(StringUtils.equals("=",opt)){
                            query.eq(column,value);
                        }else if(StringUtils.equals("like",opt)){
                            query.like(column,value);
                        }
                    });
                }

                DynamicTable dynamicTable = new DynamicTable(tableName,query).setNumberColumn(numberColumn);
                dynamicTable.setQueryWrapper(query);

                BigDecimal maxNumber = dynameicService.getMaxNumber(dynamicTable);
                return Result.ok(maxNumber);
            }

            return Result.failed("参数不能为空");

        }catch (Exception e){
            e.printStackTrace();;
            log.error(e.getMessage());
            return Result.failed();
        }

    }

}
