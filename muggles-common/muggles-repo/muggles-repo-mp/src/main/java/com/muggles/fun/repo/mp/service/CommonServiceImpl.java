package com.muggles.fun.repo.mp.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.muggles.fun.basic.Constants;
import com.muggles.fun.basic.exception.MugglesBizException;
import com.muggles.fun.basic.model.IMugglePage;
import com.muggles.fun.repo.basic.model.Muggle;
import com.muggles.fun.repo.basic.service.ICommonService;
import com.muggles.fun.repo.mp.criteria.WrapperTranslator;
import com.muggles.fun.repo.mp.mapper.CommonMapper;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * 通过用Service
 *
 * @param <M>
 * @param <T>
 */
public class CommonServiceImpl<M extends CommonMapper<T>, T> extends ServiceImpl<M, T> implements ICommonService<T> {


    /**
     * 根据 entity 条件，查询一条记录，并锁定
     *
     * @param muggle 实体对象封装操作类（可以为 null）
     * @return T
     */
    @Override
    public T oneForUpdate(Muggle<T> muggle) {
        return getOne(WrapperTranslator.translate(muggle).last("for update"));
    }

    /**
     * 根据 entity 条件，查询全部记录，并锁定
     *
     * @param muggle 实体对象封装操作类（可以为 null）
     * @return List<T>
     */
    @Override
    public List<T> listForUpdate(Muggle<T> muggle) {
        return list(WrapperTranslator.translate(muggle).last("for update"));
    }

    /**
     * 根据ID更新字为null
     *
     * @param fields 置NULL字段集合
     * @param id     实体主键
     * @return boolean
     */
    @Override
    public boolean updateFieldNullById(List<String> fields, Serializable id) {
        QueryWrapper<T> wrapper = Wrappers.query();
        //1.更新条件为空则不更新
        if (CollUtil.isEmpty(fields)) {
            return false;
        }
        //2.ID为NULL则不允许更新
        Assert.notNull(id, () -> new MugglesBizException("id不能为空"));
        //3.操作数据库update语句
        return SqlHelper.retBool(mapper().updateFieldNUll(tableName(), fields, wrapper.eq("id", id)));
    }

    /**
     * 根据ID更新字段自增，会忽略非数字类型字段值
     *
     * @param entity  实体
     * @param postive 是否添加非负判断
     * @return boolean
     */
    @Override
    public boolean updateFieldSelfById(T entity, boolean postive) {
        Object id = ReflectUtil.getFieldValue(entity, "id");
        //1.ID为NULL则不允许更新
        Assert.notNull(id, () -> new MugglesBizException("id不能为空"));
        ReflectUtil.setFieldValue(entity, "id", null);

        QueryWrapper<T> wrapper = Wrappers.<T>query().eq("id", id);
        Map<String, Object> res = toMap(wrapper, entity, true, postive);
        //2.更新条件为空则不更新
        if (CollUtil.isEmpty(res)) {
            return false;
        }

        //3.判断逻辑删除和乐观锁
        return SqlHelper.retBool(mapper().updateFieldSelf(tableName(), res, wrapper));
    }

    /**
     * 实体对象类型
     */
    public Class<T> getTClass() {
        return (Class<T>) TypeUtil.getTypeArguments(getClass())[1];
    }

    /**
     * 获取实体对象对应的表名
     *
     * @return String
     */
    @Override
    public String tableName() {
        return TableInfoHelper.getTableInfo(getTClass()).getTableName();
    }

    /**
     * 物理删除
     *
     * @param id 主键
     * @return boolean
     */
    @Override
    public boolean removePhyById(Serializable id) {
        //1.ID为NULL则不允许删除
        Assert.notNull(id, () -> new MugglesBizException("id不能为空"));
        return SqlHelper.retBool(mapper().deletePhy(tableName(), Wrappers.<T>query().eq("id", id)));
    }

    /**
     * 根据主键恢复某个逻辑删的数据
     *
     * @param id 记录主键
     * @return boolean
     */
    @Override
    public boolean recoverById(Serializable id) {
        return false;
    }

    /**
     * 获取mapper对象
     *
     * @return CommonMapper<T>
     */
    public M mapper() {
        return baseMapper;
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     * @return boolean
     */
    @Override
    public boolean save(T entity) {
        return SqlHelper.retBool(mapper().insert(entity));
    }


    /**
     * 根据 ID 选择修改
     *
     * @param entity 实体对象
     */
    @Override
    public boolean updateById(T entity) {
        return SqlHelper.retBool(mapper().updateById(entity));
    }

    /**
     * 将指定条件的记录更新诚实体非null字段
     *
     * @param t     实体记录
     * @param param 更新条件
     * @return Boolean
     */
    @Override
    public Boolean update(T t, Muggle<T> param) {
        return update(t, WrapperTranslator.translate(param));
    }

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    @Override
    public T getById(Serializable id) {
        return mapper().selectById(id);
    }

    /**
     * 根据查询条件查询实体第一条记录
     *
     * @param param 查询条件
     * @return T
     */
    @Override
    public T one(Muggle<T> param) {
        return getOne(WrapperTranslator.translate(param));
    }

    /**
     * 根据查询条件查询实体集合
     *
     * @param param 查询条件
     * @return T
     */
    @Override
    public List<T> list(Muggle<T> param) {
        return list(WrapperTranslator.translate(param));
    }

    /**
     * 根据查询条件查询实体分页集合
     *
     * @param param 查询条件
     * @return T
     */
    @Override
    public IMugglePage<T> page(Muggle<T> param) {
        return page(WrapperTranslator.toPage(param),WrapperTranslator.translate(param));
    }


    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    @Override
    public boolean removeById(Serializable id) {
        return SqlHelper.retBool(mapper().deleteById(id));
    }

    /**
     * 批量保存实体
     *
     * @param list 实体记录
     * @return Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(List<T> list) {
        return saveBatch(list, 100);
    }

    /**
     * 根据实体id批量更新实体
     *
     * @param list 实体记录
     * @return Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(List<T> list) {
        return updateBatchById(list, DEFAULT_BATCH_SIZE);
    }


    /**
     * 根据实体id批量插入或者更新实体
     *
     * @param list 实体记录
     * @return Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatchById(List<T> list) {
        return saveOrUpdateBatch(list, DEFAULT_BATCH_SIZE);
    }

    /**
     * 根据id集合批量删除记录
     *
     * @param ids 实体id集合
     * @return Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeBatchById(List<Serializable> ids) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        return removeBatchByIds(ids, 100);
    }

    /**
     * 根据条件删除实体记录
     *
     * @param param 通用查询参数
     * @return C
     */
    @Override
    public Boolean remove(Muggle<T> param) {
        return remove(WrapperTranslator.translate(param));
    }

    /**
     * 查询所有
     *
     * @see Wrappers#emptyWrapper()
     */
    @Override
    public List<T> list() {
        return mapper().selectList(Wrappers.emptyWrapper());
    }

    /**
     * 获取实体属性键值对，同时更新条件
     *
     * @param wrapper         mp查询条件
     * @param entity          包含更新属性的实体对象
     * @param ignoreNotNumber 是否忽略非数字字段
     * @param postive         是否增加非负判断
     * @return Map<String, Object>
     */
    public Map<String, Object> toMap(QueryWrapper<T> wrapper, T entity, boolean ignoreNotNumber, boolean postive) {
        //1.生成属性键值对
        Map<String, Object> result = MapUtil.newHashMap();
        List<Field> fields = TableInfoHelper.getAllFields(entity.getClass());
        //1.处理逻辑删除
        fields.stream().filter(field -> field.isAnnotationPresent(TableLogic.class)).forEach(field -> {
            wrapper.eq(WrapperTranslator.column(entity.getClass(), field.getName()), Constants.INIT);
        });
        //2.处理属性字段以及乐观锁
        fields.stream()
                .filter(field -> ObjectUtil.isNotNull(ReflectUtil.getFieldValue(entity, field)))
                .filter(field -> !ignoreNotNumber || Number.class.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    Object v = ReflectUtil.getFieldValue(entity, field);
                    if (field.isAnnotationPresent(Version.class)) {
                        wrapper.eq(WrapperTranslator.column(entity.getClass(), field.getName()), v);
                        //2.1版本号每次只能涨一个单位
                        result.put(WrapperTranslator.column(entity.getClass(), field.getName()), Constants.DEFAULT_OPT);
                        return;
                    }
                    //2.2非负判定
                    if (postive && (new BigDecimal(v.toString()).compareTo(new BigDecimal(0)) < 0)) {
                        wrapper.ge(WrapperTranslator.column(entity.getClass(), field.getName()), new BigDecimal(v.toString()).abs());
                    }
                    result.put(WrapperTranslator.column(entity.getClass(), field.getName()), v);
                });
        return result;
    }
}
