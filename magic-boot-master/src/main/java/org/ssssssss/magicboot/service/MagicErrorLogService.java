package org.ssssssss.magicboot.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.ssssssss.magicboot.entity.MagicErrorLog;
import org.ssssssss.magicboot.mapper.MagicErrorLogMapper;

@Service
public class MagicErrorLogService extends ServiceImpl<MagicErrorLogMapper, MagicErrorLog> {

    /**
     * 根据ID查询错误日志详情
     */
    public MagicErrorLog getById(String id) {
        return super.getById(id);
    }

    /**
     * 保存错误日志
     */
    public boolean saveErrorLog(MagicErrorLog errorLog) {
        return super.save(errorLog);
    }
}
