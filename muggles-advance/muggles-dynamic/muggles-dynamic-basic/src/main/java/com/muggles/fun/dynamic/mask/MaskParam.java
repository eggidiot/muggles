package com.muggles.fun.dynamic.mask;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.muggles.fun.dynamic.util.CompatibleObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 面具参数
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangjun</a>
 * @since 0.9.0
 */
@Slf4j
public class MaskParam extends ArrayList<JSONObject> implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public List<AbstractMask> getActualMasks() {
        if (CollectionUtil.isEmpty(this)) {
            return Collections.emptyList();
        }
        try {
            return this.stream().map(x -> {
                try {
                    final String key = x.getString("key");
                    Class<? extends AbstractMask> maskClz = AbstractMask.getRepository().get(key);
                    return CompatibleObjectMapper.INSTANCE.readValue(x.toString(), maskClz);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
        } catch (Throwable e) {
            throw new RuntimeException(String.format("Mask解析失败！%s", e.getMessage()), e);
        }
    }

}
