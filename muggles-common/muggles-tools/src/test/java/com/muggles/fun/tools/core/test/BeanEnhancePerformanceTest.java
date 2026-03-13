package com.muggles.fun.tools.core.test;

import cn.hutool.json.JSONUtil;
import com.muggles.fun.tools.core.bean.BeanExtUtil;
import com.muggles.fun.tools.core.bean.BeanEnhancer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * cglib vs ByteBuddy 动态属性增强性能对比测试
 *
 * 如果 cglib 在高版本 JDK(17+) 报 InaccessibleObjectException，需要添加 JVM 参数：
 * --add-opens java.base/java.lang=ALL-UNNAMED
 */
public class BeanEnhancePerformanceTest {

    // 批量测试循环次数
    private static final int BATCH_SIZE = 1000;
    // 预热次数
    private static final int WARMUP_COUNT = 5;
    // cglib 是否可用
    private static boolean cglibAvailable = true;

    public static void main(String[] args) throws Exception {
        System.out.println("========== 动态属性增强性能对比测试 ==========\n");

        // 检测 cglib 是否可用（高版本 JDK 模块系统限制）
        checkCglibAvailable();

        // 准备少量属性（3个）
        Map<String, Object> fewProps = new LinkedHashMap<>();
        fewProps.put("nickname", "测试用户");
        fewProps.put("score", 99.5);
        fewProps.put("active", true);

        // 准备大量属性（20个）
        Map<String, Object> manyProps = new LinkedHashMap<>();
        for (int i = 1; i <= 20; i++) {
            manyProps.put("field" + i, "value" + i);
        }

        // 结果收集
        List<String[]> results = new ArrayList<>();

        // ==================== 1. 单次增强（冷启动）- 少量属性 ====================
        System.out.println("--- 1. 单次增强（冷启动）- 3个属性 ---");

        String cglibJson1 = "N/A";
        long cglibSingle3 = -1;
        if (cglibAvailable) {
            AccountService src1 = new AccountService();
            long t1 = System.nanoTime();
            Object cglibResult1 = BeanExtUtil.enhance(src1, new LinkedHashMap<>(fewProps));
            cglibSingle3 = System.nanoTime() - t1;
            cglibJson1 = JSONUtil.toJsonStr(cglibResult1);
            System.out.println("  cglib      : " + ns2ms(cglibSingle3) + " ms | " + cglibJson1);
        } else {
            System.out.println("  cglib      : [不可用]");
        }

        AccountService src2 = new AccountService();
        long t1 = System.nanoTime();
        Object buddyResult1 = BeanEnhancer.enhance(src2, new LinkedHashMap<>(fewProps));
        long buddySingle3 = System.nanoTime() - t1;
        String buddyJson1 = JSONUtil.toJsonStr(buddyResult1);
        System.out.println("  ByteBuddy  : " + ns2ms(buddySingle3) + " ms | " + buddyJson1);

        results.add(new String[]{"单次-冷启动", "3", formatMs(cglibSingle3), ns2ms(buddySingle3), cglibAvailable ? checkEqual(cglibJson1, buddyJson1) : "N/A"});

        // ==================== 2. 单次增强（冷启动）- 大量属性 ====================
        System.out.println("\n--- 2. 单次增强（冷启动）- 20个属性 ---");

        long cglibSingle20 = -1;
        if (cglibAvailable) {
            AccountService src3 = new AccountService();
            t1 = System.nanoTime();
            BeanExtUtil.enhance(src3, new LinkedHashMap<>(manyProps));
            cglibSingle20 = System.nanoTime() - t1;
            System.out.println("  cglib      : " + ns2ms(cglibSingle20) + " ms");
        } else {
            System.out.println("  cglib      : [不可用]");
        }

        AccountService src4 = new AccountService();
        t1 = System.nanoTime();
        BeanEnhancer.enhance(src4, new LinkedHashMap<>(manyProps));
        long buddySingle20 = System.nanoTime() - t1;
        System.out.println("  ByteBuddy  : " + ns2ms(buddySingle20) + " ms");

        results.add(new String[]{"单次-冷启动", "20", formatMs(cglibSingle20), ns2ms(buddySingle20), "N/A"});

        // ==================== 3. 批量增强（热路径）- 少量属性 ====================
        System.out.println("\n--- 3. 批量增强（" + BATCH_SIZE + "次）- 3个属性 ---");

        // 预热
        for (int i = 0; i < WARMUP_COUNT; i++) {
            if (cglibAvailable) {
                BeanExtUtil.enhance(new AccountService(), new LinkedHashMap<>(fewProps));
            }
            BeanEnhancer.enhance(new AccountService(), new LinkedHashMap<>(fewProps));
        }

        long cglibBatch3 = -1;
        if (cglibAvailable) {
            t1 = System.nanoTime();
            for (int i = 0; i < BATCH_SIZE; i++) {
                BeanExtUtil.enhance(new AccountService(), new LinkedHashMap<>(fewProps));
            }
            cglibBatch3 = System.nanoTime() - t1;
            System.out.println("  cglib      : " + ns2ms(cglibBatch3) + " ms (avg " + ns2ms(cglibBatch3 / BATCH_SIZE) + " ms/次)");
        } else {
            System.out.println("  cglib      : [不可用]");
        }

        t1 = System.nanoTime();
        for (int i = 0; i < BATCH_SIZE; i++) {
            BeanEnhancer.enhance(new AccountService(), new LinkedHashMap<>(fewProps));
        }
        long buddyBatch3 = System.nanoTime() - t1;
        System.out.println("  ByteBuddy  : " + ns2ms(buddyBatch3) + " ms (avg " + ns2ms(buddyBatch3 / BATCH_SIZE) + " ms/次)");

        results.add(new String[]{"批量-" + BATCH_SIZE + "次", "3", formatMs(cglibBatch3), ns2ms(buddyBatch3), "N/A"});

        // ==================== 4. 批量增强（热路径）- 大量属性 ====================
        System.out.println("\n--- 4. 批量增强（" + BATCH_SIZE + "次）- 20个属性 ---");

        // 预热
        for (int i = 0; i < WARMUP_COUNT; i++) {
            if (cglibAvailable) {
                BeanExtUtil.enhance(new AccountService(), new LinkedHashMap<>(manyProps));
            }
            BeanEnhancer.enhance(new AccountService(), new LinkedHashMap<>(manyProps));
        }

        long cglibBatch20 = -1;
        if (cglibAvailable) {
            t1 = System.nanoTime();
            for (int i = 0; i < BATCH_SIZE; i++) {
                BeanExtUtil.enhance(new AccountService(), new LinkedHashMap<>(manyProps));
            }
            cglibBatch20 = System.nanoTime() - t1;
            System.out.println("  cglib      : " + ns2ms(cglibBatch20) + " ms (avg " + ns2ms(cglibBatch20 / BATCH_SIZE) + " ms/次)");
        } else {
            System.out.println("  cglib      : [不可用]");
        }

        t1 = System.nanoTime();
        for (int i = 0; i < BATCH_SIZE; i++) {
            BeanEnhancer.enhance(new AccountService(), new LinkedHashMap<>(manyProps));
        }
        long buddyBatch20 = System.nanoTime() - t1;
        System.out.println("  ByteBuddy  : " + ns2ms(buddyBatch20) + " ms (avg " + ns2ms(buddyBatch20 / BATCH_SIZE) + " ms/次)");

        results.add(new String[]{"批量-" + BATCH_SIZE + "次", "20", formatMs(cglibBatch20), ns2ms(buddyBatch20), "N/A"});

        // ==================== 5. 功能正确性验证 ====================
        System.out.println("\n--- 5. 功能正确性验证 ---");

        AccountService verifySource = new AccountService();
        verifySource.setAmount(888);
        Map<String, Object> verifyProps = new LinkedHashMap<>();
        verifyProps.put("name", "验证用户");
        verifyProps.put("level", 5);

        if (cglibAvailable) {
            Object cglibObj = BeanExtUtil.enhance(verifySource, new LinkedHashMap<>(verifyProps));
            String cglibJsonVerify = JSONUtil.toJsonStr(cglibObj);
            System.out.println("  cglib     JSON: " + cglibJsonVerify);
        } else {
            System.out.println("  cglib     JSON: [不可用]");
        }

        Object buddyObj = BeanEnhancer.enhance(verifySource, new LinkedHashMap<>(verifyProps));
        String buddyJsonVerify = JSONUtil.toJsonStr(buddyObj);
        System.out.println("  ByteBuddy JSON: " + buddyJsonVerify);

        // ==================== 汇总表格 ====================
        System.out.println("\n========== 汇总对比表格 ==========");
        System.out.printf("%-20s %-8s %-15s %-15s %-10s %-10s%n",
                "场景", "属性数", "cglib(ms)", "ByteBuddy(ms)", "倍率", "一致性");
        System.out.println("-".repeat(80));
        for (String[] r : results) {
            String ratio;
            if ("-".equals(r[2])) {
                ratio = "N/A";
            } else {
                double cglibMs = Double.parseDouble(r[2]);
                double buddyMs = Double.parseDouble(r[3]);
                ratio = buddyMs > 0 ? String.format("%.2fx", cglibMs / buddyMs) : "N/A";
            }
            System.out.printf("%-20s %-8s %-15s %-15s %-10s %-10s%n",
                    r[0], r[1], r[2], r[3], ratio, r[4]);
        }
        System.out.println("\n(倍率 = cglib耗时/ByteBuddy耗时，>1 表示 ByteBuddy 更快)");
        if (!cglibAvailable) {
            System.out.println("\n[提示] cglib 在当前 JDK 版本下不可用，请添加 JVM 参数后重试：");
            System.out.println("  --add-opens java.base/java.lang=ALL-UNNAMED");
        }
        System.out.println("========== 测试完成 ==========");
    }

    /**
     * 检测 cglib 是否可用
     */
    private static void checkCglibAvailable() {
        try {
            AccountService probe = new AccountService();
            Map<String, Object> probeProps = new LinkedHashMap<>();
            probeProps.put("_probe", "test");
            BeanExtUtil.enhance(probe, probeProps);
        } catch (Exception e) {
            cglibAvailable = false;
            System.out.println("[警告] cglib 不可用: " + e.getCause().getMessage());
            System.out.println("[提示] 添加 JVM 参数可启用 cglib: --add-opens java.base/java.lang=ALL-UNNAMED\n");
        }
    }

    private static String ns2ms(long nanos) {
        return String.format("%.3f", nanos / 1_000_000.0);
    }

    private static String formatMs(long nanos) {
        return nanos < 0 ? "-" : ns2ms(nanos);
    }

    private static String checkEqual(String json1, String json2) {
        return JSONUtil.parseObj(json1).equals(JSONUtil.parseObj(json2)) ? "YES" : "NO";
    }
}
