package com.super_m.uid.generator;

import com.super_m.uid.constant.UidConstant;
import com.super_m.uid.exception.SystemRuntimeException;
//import lombok.extern.slf4j.Slf4j;

/**
 * 描述 : 分布式id生成器,基于twiter的snowflake
 *
 * @author ma.chao
 */
//@Slf4j
public class SnowflakeUidGenerator implements IUidGenerator<Long> {

    /**
     * 描述 : 机器ID( 0 - 31 )
     */
    private long workerId;

    /**
     * 描述 : 数据中心ID( 0 - 31 )
     */
    private long datacenterId;

    /**
     * 描述 : 序列号( 0 - 4095)
     */
    private long sequence = 0L;

    /**
     * 描述 : 上次生产id时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 描述 : 构造函数
     *
     * @param workerId     workerId
     * @param datacenterId datacenterId
     */
    public SnowflakeUidGenerator(long workerId, long datacenterId) {
        setWorkerId(workerId);
        setDatacenterId(datacenterId);
//        log.info("datacenterId:{},workerId:{}", this.datacenterId, this.workerId);
    }

    public void setWorkerId(long workerId) {
        // sanity check for workerId
        if (workerId > UidConstant.MAX_WORKER_ID || workerId < 0)
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", UidConstant.MAX_WORKER_ID));
        this.workerId = workerId;
    }

    public void setDatacenterId(long datacenterId) {
        if (datacenterId > UidConstant.MAX_DATACENTER_ID || datacenterId < 0)
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", UidConstant.MAX_DATACENTER_ID));
        this.datacenterId = datacenterId;
    }

    /**
     * 描述 : 下一个ID
     *
     * @return ID
     */
    public synchronized Long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp)
            throw new SystemRuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));

        if (lastTimestamp == timestamp) {
            if (0L == (sequence = ++sequence & UidConstant.SEQUENCE_MASK))
                timestamp = tilNextMillis(lastTimestamp);
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - UidConstant.TWEPOCH) << UidConstant.TIMESTAMP_LEFT_SHIFT) | (datacenterId << UidConstant.DATACENTER_ID_SHIFT) | (workerId << UidConstant.WORKER_ID_SHIFT) | sequence;
    }

    @Override
    public String parseId(Long id) {
        long recoverWorkId = id >>> UidConstant.WORKER_ID_SHIFT & UidConstant.calaBitMask(UidConstant.WORKER_ID_BITS);
        long recoverDataCenterId = id >>> UidConstant.DATACENTER_ID_SHIFT & UidConstant.calaBitMask(UidConstant.DATACENTER_ID_BITS);
        long timestamp = UidConstant.TWEPOCH + id >>> UidConstant.TIMESTAMP_LEFT_SHIFT;
        return String.format("{\"UID\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"datacenterId\":\"%d\"}",
                id, timestamp, recoverWorkId, recoverDataCenterId);
    }

    /**
     * 描述 : 获得下一个毫秒数
     *
     * @param lastTimestampParam lastTimestampParam
     * @return 下一个毫秒数
     */
    protected long tilNextMillis(long lastTimestampParam) {
        long timestamp;
        do timestamp = timeGen(); while (timestamp <= lastTimestampParam);
        return timestamp;
    }

    /**
     * 描述 : 获得当前时间毫秒数
     *
     * @return 当前时间毫秒数
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

}

