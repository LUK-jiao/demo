package com.example.demo.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, 30);
        Date expiresAt = calendar.getTime();
        this.strictInsertFill(metaObject, "createdAt",
                Date.class, now);
        this.strictInsertFill(metaObject, "updatedAt",
                Date.class, now);
        this.strictInsertFill(metaObject, "expiresAt", Date.class, expiresAt);
        this.strictInsertFill(metaObject, "used", Boolean.class,false);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updatedAt",new Date());
    }
}
