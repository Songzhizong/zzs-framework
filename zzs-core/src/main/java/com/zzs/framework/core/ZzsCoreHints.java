package com.zzs.framework.core;

import com.zzs.framework.core.cache.serialize.JsonValueSerializer;
import com.zzs.framework.core.cache.serialize.StringKeySerializer;
import com.zzs.framework.core.cache.serialize.StringValueSerializer;
import com.zzs.framework.core.event.BaseEvent;
import com.zzs.framework.core.event.EventTuple;
import com.zzs.framework.core.event.GeneralEvent;
import com.zzs.framework.core.event.impl.MongoEventLock;
import com.zzs.framework.core.event.impl.MongoEventTemp;
import com.zzs.framework.core.lang.Triple;
import com.zzs.framework.core.lang.Tuple;
import com.zzs.framework.core.trace.OperationLog;
import com.zzs.framework.core.trace.Operator;
import com.zzs.framework.core.trace.TraceContext;
import com.zzs.framework.core.transmission.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;

/**
 * @author 宋志宗 on 2022/9/20
 */
@NativeHint(
  types = {
    @TypeHint(types = JsonValueSerializer.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = StringKeySerializer.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = StringValueSerializer.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = MongoEventLock.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = MongoEventTemp.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = BaseEvent.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = EventTuple.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = GeneralEvent.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = OperationLog.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = Operator.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = TraceContext.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = Triple.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = Tuple.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = BasicResult.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = Result.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = ListResult.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = PageResult.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = Paging.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = Sort.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
    @TypeHint(types = SortablePaging.class, access = {TypeAccess.PUBLIC_FIELDS, TypeAccess.DECLARED_FIELDS, TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
  }
)
@Configuration
@ConditionalOnClass({NativeHint.class, TypeHint.class})
public class ZzsCoreHints implements NativeConfiguration {

}
