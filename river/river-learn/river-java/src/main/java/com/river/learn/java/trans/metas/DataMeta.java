package com.river.learn.java.trans.metas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 17822
 */
@Data
@Accessors(chain = true)
public class DataMeta {

    /**
     * 公平锁（按照线程顺序执行）
     */
    private Lock lock = new ReentrantLock(true);

    private Condition condition;

    private boolean finished;

    private List<Map<String, Object>> datas;

    public DataMeta() {
        this.condition = this.lock.newCondition();
        this.finished = false;
        this.datas = new ArrayList();
    }

}

