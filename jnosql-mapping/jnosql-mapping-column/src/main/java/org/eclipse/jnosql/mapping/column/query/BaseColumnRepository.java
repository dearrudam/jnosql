/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.eclipse.jnosql.mapping.column.query;


import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Sort;
import org.eclipse.jnosql.communication.Params;
import org.eclipse.jnosql.communication.column.ColumnDeleteQuery;
import org.eclipse.jnosql.communication.column.ColumnDeleteQueryParams;
import org.eclipse.jnosql.communication.column.ColumnObserverParser;
import org.eclipse.jnosql.communication.column.ColumnQuery;
import org.eclipse.jnosql.communication.column.ColumnQueryParams;
import org.eclipse.jnosql.communication.column.DeleteQueryParser;
import org.eclipse.jnosql.communication.column.SelectQueryParser;
import org.eclipse.jnosql.communication.query.DeleteQuery;
import org.eclipse.jnosql.communication.query.SelectQuery;
import org.eclipse.jnosql.communication.query.method.DeleteMethodProvider;
import org.eclipse.jnosql.communication.query.method.SelectMethodProvider;
import org.eclipse.jnosql.mapping.Converters;
import org.eclipse.jnosql.mapping.column.JNoSQLColumnTemplate;
import org.eclipse.jnosql.mapping.column.MappingColumnQuery;
import org.eclipse.jnosql.mapping.reflection.EntityMetadata;
import org.eclipse.jnosql.mapping.repository.DynamicReturn;
import org.eclipse.jnosql.mapping.util.ParamsBinder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class BaseColumnRepository<T> {

    protected abstract Converters getConverters();

    protected abstract EntityMetadata getEntityMetadata();

    protected abstract JNoSQLColumnTemplate getTemplate();

    private ColumnObserverParser parser;

    private ParamsBinder paramsBinder;


    private static final SelectQueryParser SELECT_PARSER = new SelectQueryParser();

    private static final DeleteQueryParser DELETE_PARSER = new DeleteQueryParser();

    protected ColumnQuery getQuery(Method method, Object[] args) {
        SelectMethodProvider selectMethodFactory = SelectMethodProvider.INSTANCE;
        SelectQuery selectQuery = selectMethodFactory.apply(method, getEntityMetadata().getName());
        ColumnQueryParams queryParams = SELECT_PARSER.apply(selectQuery, getParser());
        ColumnQuery query = queryParams.query();
        Params params = queryParams.params();
        getParamsBinder().bind(params, args, method);
        return updateQueryDynamically(args, query);
    }

    protected ColumnDeleteQuery getDeleteQuery(Method method, Object[] args) {
        DeleteMethodProvider deleteMethodFactory = DeleteMethodProvider.INSTANCE;
        DeleteQuery deleteQuery = deleteMethodFactory.apply(method, getEntityMetadata().getName());
        ColumnDeleteQueryParams queryParams = DELETE_PARSER.apply(deleteQuery, getParser());
        ColumnDeleteQuery query = queryParams.query();
        Params params = queryParams.params();
        getParamsBinder().bind(params, args, method);
        return query;
    }


    protected ColumnObserverParser getParser() {
        if (parser == null) {
            this.parser = new RepositoryColumnObserverParser(getEntityMetadata());
        }
        return parser;
    }

    protected ParamsBinder getParamsBinder() {
        if (Objects.isNull(paramsBinder)) {
            this.paramsBinder = new ParamsBinder(getEntityMetadata(), getConverters());
        }
        return paramsBinder;
    }

    protected Object executeQuery(Method method, Object[] args, Class<?> typeClass, ColumnQuery query) {
        DynamicReturn<?> dynamicReturn = DynamicReturn.builder()
                .withClassSource(typeClass)
                .withMethodSource(method)
                .withResult(() -> getTemplate().select(query))
                .withSingleResult(() -> getTemplate().singleResult(query))
                .withPagination(DynamicReturn.findPagination(args).orElse(null))
                .withStreamPagination(streamPagination(query))
                .withSingleResultPagination(getSingleResult(query))
                .withPage(getPage(query))
                .build();
        return dynamicReturn.execute();
    }

    protected Function<Pageable, Page<T>> getPage(ColumnQuery query) {
        return p -> getTemplate().select(ColumnQueryPagination.of(query, p));
    }

    protected Function<Pageable, Optional<T>> getSingleResult(ColumnQuery query) {
        return p -> {
            return getTemplate().singleResult(query);
        };
    }

    protected Function<Pageable, Stream<T>> streamPagination(ColumnQuery query) {
        return p -> {
            return getTemplate().select(query);
        };
    }


    protected ColumnQuery updateQueryDynamically(Object[] args, ColumnQuery query) {
        Optional<Pageable> pageable = DynamicReturn.findPagination(args);

        return pageable.<ColumnQuery>map(p -> {
            long size = p.size();
            long skip = size * (p.page() - 1);
            List<Sort> sorts = query.sorts();
            if (!p.sorts().isEmpty()) {
                sorts = new ArrayList<>(query.sorts());
                sorts.addAll(p.sorts());
            }
            return new MappingColumnQuery(sorts, size, skip,
                    query.condition().orElse(null), query.columnFamily());
        }).orElse(query);
    }

}
