package com.river.learn.java.trans.metas;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 17822
 */
@Data
@Accessors(chain = true)
public abstract class  StepMeta  implements  Runnable{

    /**
     * 步骤id
     */
    private String id;
    /**
     * 步骤名称
     */
    private String name;
    /**
     * 步骤类型
     */
    private String type;

    /**
     * 步骤元数据
     */
    private DataMeta dataMeta = new DataMeta();

    /**
     * 上一步骤
     */
    private List<StepMeta> preSteps = new ArrayList();

    /**
     * 下一步骤
     */
    private List<StepMeta> nextSteps = new ArrayList();

    /**
     * 步骤是否运行中
     */
    private boolean isRuning;

    /**
     * 每个流程步骤执行方法
     * @return
     * @throws Exception
     */
    protected abstract boolean process() throws Exception;

    /**
     * 步骤开始
     */
    public void onStart(){
        System.out.println(this.getName()+"节点开始");
    }

    @Override
    public void run() {

        try {
            this.onStart();
            boolean process = this.process();
            this.onFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 步骤结束
     */
    public void onFinish(){
        System.out.println(this.getName()+"节点结束");
    }


}
