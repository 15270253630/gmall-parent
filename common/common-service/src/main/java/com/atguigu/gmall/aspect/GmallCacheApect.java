package com.atguigu.gmall.aspect;

import cn.hutool.core.util.ArrayUtil;
import com.atguigu.gmall.annotation.GmallCache;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.enums.CacheTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class GmallCacheApect {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.annotation.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point) {
        Object proceed = null;

        // 前置

        // 获取类的信息
        Object[] args = point.getArgs();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();

        String name = method.getName();
        Class returnType = method.getReturnType();
        GmallCache annotation = method.getAnnotation(GmallCache.class);
        String prefix = annotation.prefix();
        CacheTypeEnum cacheTypeEnum = annotation.cacheType();
        String suffix = annotation.suffix();

        // 拼接取得key的id值
        String argKey = "";
        if (ArrayUtil.isNotEmpty(args)) {
            for (int i = 0; i < args.length; i++) {
                // 判断参数是否非基本类型包括string
                if (isCustomObj(args[i])) {
                    argKey = argKey + args[i].getClass().getName() + ":";
                }
                argKey = argKey + parameters[i].getName() + "^" + args[i] + ":";
            }
        }

        // 拼接key
        String key = "GmallCache:" + prefix + argKey + suffix;

        // 从缓存中获取
        proceed = cacheHit(key);

        if (proceed == null) {
            log.info("缓存中没有数据库中查询");

            RLock lock = redissonClient.getLock("lock");
//            String lockTag = UUID.randomUUID().toString();
//            Boolean lock = redisTemplate.opsForValue().setIfAbsent("GmallCache:" + prefix + argKey + "lock", lockTag, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);

            try {
                lock.lock();
                log.info("抢到锁，进入数据库查询");
                // 执行目标方法
                proceed = point.proceed(args);
                // 后置
                if (proceed != null) {
                    redisTemplate.opsForValue().set(key, proceed,
                            RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                } else {

                    redisTemplate.opsForValue().set(key, null,
                            RedisConst.SKU_VALUE_TEMPORARY_TIMEOUT, TimeUnit.MINUTES);
                }
//                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";// 脚本查询是否存在存在则删除否则返回0
//                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//                redisScript.setResultType(Long.class);
//                redisScript.setScriptText(script);
//                redisTemplate.execute(redisScript, Arrays.asList("GmallCache:" + prefix + argKey + "lock"), lockTag);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }

        return proceed;
    }

    private boolean isCustomObj(Object obj) {
        boolean isCustom = true;

        if (obj instanceof Long ||
                obj instanceof Integer ||
                obj instanceof BigDecimal ||
                obj instanceof Double ||
                obj instanceof Short ||
                obj instanceof Byte ||
                obj instanceof Float ||
                obj instanceof Character) {
            isCustom = false;
        }

        return isCustom;
    }


    private Object cacheHit(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    //    private Object setCache(String key, Object value, CacheTypeEnum type) {
//
//    }


//    private Object cacheHit(String key, CacheTypeEnum type) {
//        Object o = null;
//        switch (type) {
//            case STR:
//                o = redisTemplate.opsForValue().get(key);
//                break;
//            case LIST:
//                o = redisTemplate.opsForList().range(key,0,-1);
//                break;
//            case HASH:
////                0 = redisTemplate.opsForHash().get;
//                break;
//        }
//
//        return o;
//    }


}
