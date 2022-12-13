package cn.tedu.mall.seckill.timer.config;

import cn.tedu.mall.seckill.timer.job.SeckillBloomInitialJob;
import cn.tedu.mall.seckill.timer.job.SeckillInitialJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    // 向Spring容器中保存JobDetail
    @Bean
    public JobDetail initJobDetail(){
        return JobBuilder.newJob(SeckillInitialJob.class)
                .withIdentity("initSeckill")
                .storeDurably()
                .build();
    }

    // 向Spring容器中保存Trigger
    @Bean
    public Trigger initTrigger(){
        // 12:00  14:00 18:00秒杀的话,各提前5分钟秒杀预热的话
        // 0 55 11,13,17 * * ?
        // 为了方便学习和观察测试,我们设计每分钟都运行一次
        CronScheduleBuilder cron=
                CronScheduleBuilder.cronSchedule("0 0/1 * * * ?");
        return TriggerBuilder.newTrigger()
                .forJob(initJobDetail())
                .withIdentity("initTrigger")
                .withSchedule(cron)
                .build();
    }


    // 向Spring容器中保存JobDetail
    @Bean
    //               ↓↓↓↓↓
    public JobDetail bloomInitJobDetail(){
        //                              ↓↓↓↓↓
        return JobBuilder.newJob(SeckillBloomInitialJob.class)
                //             ↓↓↓↓↓
                .withIdentity("bloomInitSeckill")
                .storeDurably()
                .build();
    }

    // 向Spring容器中保存Trigger
    @Bean
    //             ↓↓↓↓↓
    public Trigger bloomInitTrigger(){
        CronScheduleBuilder cron=
                CronScheduleBuilder.cronSchedule("0 0/1 * * * ?");
        return TriggerBuilder.newTrigger()
                //      ↓↓↓↓↓
                .forJob(bloomInitJobDetail())
                //             ↓↓↓↓↓
                .withIdentity("bloomInitTrigger")
                .withSchedule(cron)
                .build();
    }



}
