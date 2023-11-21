/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.muggles.fun.basic.model;

import com.muggles.fun.basic.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T> 返回值参数
 * @author y
 */
@ToString
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {

    /**
     * 返回标记：成功标记=0，失败标记=1
     */
    private int code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 消息体业务数据
     */
    private T data;

    /**
     * 本次响应时的系统时间
     */
    private long t = System.currentTimeMillis();
    /**
     * 当前请求序列号
     */
    private String requestId;

    /**
     * 网络请求成功返回，默认不包含任何业务数据
     * 如果没有业务数据返回可以设置业务值为布尔行
     *
     * @param <T>
     * @return
     */
    public static <T> R<T> ok() {
        return restResult(null, Constants.SUCCESS, null);
    }

    /**
     * 网络请求成功返回，设置返回业务数据
     *
     * @param data 业务数据
     * @param <T>
     * @return
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, Constants.SUCCESS, null);
    }

    /**
     * 网络请求成功返回，设置业务数据和提示消息
     * 不建议使用，提示消息一般和错误返回一起使用
     *
     * @param data 业务数据
     * @param msg  提示消息
     * @param <T>
     * @return
     */
    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, Constants.SUCCESS, msg);
    }

    /**
     * 网络请求失败返回，表示本次请求行为与预期不一致
     * 错误返回最好包含错误提示消息，不建议使用
     *
     * @param <T>
     * @return
     */
    public static <T> R<T> failed() {
        return restResult(null, Constants.FAIL, null);
    }

    /**
     * 网络请求失败返回，表示本次请求行为与预期不一致，错误提示问题
     *
     * @param msg 提示消息
     * @param <T>
     * @return
     */
    public static <T> R<T> failed(String msg) {
        return restResult(null, Constants.FAIL, msg);
    }

    /**
     * 网络请求失败返回，表示本次请求行为与预期不一致，错误提示问题
     *
     * @param code 业务返回值
     * @param msg  错误提示
     * @param <T>
     * @return
     */
    public static <T> R<T> failed(int code, String msg) {
        return restResult(null, code, msg);
    }

    /**
     * 网络请求结果，表示本次网络请求成功或者失败，并设置成功的业务数据或者失败的提示消息
     *
     * @param data 业务数据
     * @param code 本次请求结果编码，200为成功，其他都为失败,默认失败为500
     * @param msg  错误提示消息
     * @param <T>
     * @return
     */
    private static <T> R<T> restResult(T data, int code, String msg) {
        return new R().setData(data).setCode(code).setMsg(msg);
    }

}
