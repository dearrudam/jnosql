/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *  You may elect to redistribute this code under either of these licenses.
 *  Contributors:
 *  Otavio Santana
 */
package org.eclipse.jnosql.communication.query.method;

import org.antlr.v4.runtime.tree.ParseTree;
import jakarta.data.repository.Sort;
import jakarta.data.repository.Direction;
import org.eclipse.jnosql.communication.query.SelectQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class FindByMethodQueryProvider extends AbstractMethodQueryProvider implements BiFunction<String, String, SelectQuery> {

    private final List<Sort> sorts = new ArrayList<>();

    @Override
    public SelectQuery apply(String query, String entity) {
        Objects.requireNonNull(query, " query is required");
        Objects.requireNonNull(entity, " entity is required");
        runQuery(MethodQuery.of(query).get());
        return new MethodSelectQuery(entity, sorts, where);
    }

    @Override
    public void exitOrderName(MethodParser.OrderNameContext ctx) {
        sorts.add(this.sort(ctx));
    }

    @Override
    Function<MethodParser, ParseTree> getParserTree() {
        return MethodParser::findBy;
    }

    private Sort sort(MethodParser.OrderNameContext context) {
        String text = context.variable().getText();
        Direction type = context.desc() == null ? Direction.ASC : Direction.DESC;
        return Sort.of(getFormatField(text), type, false);
    }
}
