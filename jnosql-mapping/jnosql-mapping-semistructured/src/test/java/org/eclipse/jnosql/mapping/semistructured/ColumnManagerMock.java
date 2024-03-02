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
package org.eclipse.jnosql.mapping.semistructured;

import org.eclipse.jnosql.communication.semistructured.CommunicationEntity;
import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.communication.semistructured.DeleteQuery;
import org.eclipse.jnosql.communication.semistructured.SelectQuery;

import java.time.Duration;
import java.util.stream.Stream;

public class ColumnManagerMock implements DatabaseManager {


    @Override
    public String name() {
        return null;
    }

    @Override
    public CommunicationEntity insert(CommunicationEntity entity) {
        return null;
    }

    @Override
    public CommunicationEntity insert(CommunicationEntity entity, Duration ttl) {
        return null;
    }

    @Override
    public Iterable<CommunicationEntity> insert(Iterable<CommunicationEntity> entities) {
        return null;
    }

    @Override
    public Iterable<CommunicationEntity> insert(Iterable<CommunicationEntity> entities, Duration ttl) {
        return null;
    }

    @Override
    public CommunicationEntity update(CommunicationEntity entity) {
        return null;
    }

    @Override
    public Iterable<CommunicationEntity> update(Iterable<CommunicationEntity> entities) {
        return null;
    }

    @Override
    public void delete(DeleteQuery query) {

    }

    @Override
    public Stream<CommunicationEntity> select(SelectQuery query) {
        return null;
    }

    @Override
    public long count(String entity) {
        return 0;
    }

    @Override
    public void close() {

    }
}
