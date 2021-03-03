package com.river.common.core.util;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树工具类
 * @author river
 */
@UtilityClass
public class TreeUtils {

    public List<TreeNode> buildTree(List<TreeNode> list) {
        List nodes = new ArrayList();

        Map nodeMaps = new HashMap();
        for (TreeNode node : list) {
            nodeMaps.put(node.getSid(), node);

            if ( StringUtils.isBlank(node.getParentId()) || "-1".equals(node.getParentId()) ) {
                nodes.add(node);
            }

        }

        for (TreeNode node : list) {
            if (StringUtils.isNotBlank(node.getParentId()) && (!"-1".equals(node.getParentId()))) {
                TreeNode parent = (TreeNode)nodeMaps.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return nodes;
    }

    @Data
    @Accessors(chain = true)
    public static class TreeNode<T>{
        private String sid;
        private String parentId;
        private String displayName;
        private String code;
        //图标
        private String icon;
        //状态
        private String status;
        //是否选中
        private boolean selected;
        //是否展开
        private boolean expand;
        //是否禁用
        private boolean disabled;

        private T refData;
        //其他属性
        private Map<String, Object> attributes = new HashMap();
        //子节点
        private List<TreeNode> children = new ArrayList();
    }

}
