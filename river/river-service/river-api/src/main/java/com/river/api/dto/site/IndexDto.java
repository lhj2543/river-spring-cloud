package com.river.api.dto.site;

import com.river.api.entity.site.SiteAttrs;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author river
 */
@Data
@EqualsAndHashCode
public class IndexDto implements Serializable {

    private List<SiteAttrs> siteAttrs;

    private Map<String,Object> reserve = new HashMap<String,Object>();

}
