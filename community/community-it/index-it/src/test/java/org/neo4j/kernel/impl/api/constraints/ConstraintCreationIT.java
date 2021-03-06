/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.api.constraints;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import org.neo4j.graphdb.ConstraintViolationException;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.Iterables;
import org.neo4j.kernel.api.impl.index.storage.layout.IndexFolderLayout;
import org.neo4j.kernel.api.index.IndexProvider;
import org.neo4j.kernel.impl.api.index.IndexProviderMap;
import org.neo4j.test.rule.EmbeddedDatabaseRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.neo4j.graphdb.factory.GraphDatabaseSettings.SchemaIndex.NATIVE20;
import static org.neo4j.graphdb.factory.GraphDatabaseSettings.default_schema_provider;

public class ConstraintCreationIT
{
    @Rule
    public EmbeddedDatabaseRule db = new EmbeddedDatabaseRule().startLazily();

    private static final Label LABEL = Label.label( "label1" );
    private static final long indexId = 1;

    @Test
    public void shouldNotLeaveLuceneIndexFilesHangingAroundIfConstraintCreationFails()
    {
        // given
        db.withSetting( default_schema_provider, NATIVE20.providerName() ); // <-- includes Lucene sub-provider
        attemptAndFailConstraintCreation();

        // then
        IndexProvider indexProvider =
                db.getDependencyResolver().resolveDependency( IndexProviderMap.class ).getDefaultProvider();
        File indexDir = indexProvider.directoryStructure().directoryForIndex( indexId );

        assertFalse( new IndexFolderLayout( indexDir ).getIndexFolder().exists() );
    }

    @Test
    public void shouldNotLeaveNativeIndexFilesHangingAroundIfConstraintCreationFails()
    {
        // given
        attemptAndFailConstraintCreation();

        // then
        IndexProvider indexProvider =
                db.getDependencyResolver().resolveDependency( IndexProviderMap.class ).getDefaultProvider();
        File indexDir = indexProvider.directoryStructure().directoryForIndex( indexId );

        assertFalse( indexDir.exists() );
    }

    private void attemptAndFailConstraintCreation()
    {
        try ( Transaction tx = db.beginTx() )
        {
            for ( int i = 0; i < 2; i++ )
            {
                Node node1 = db.createNode( LABEL );
                node1.setProperty( "prop", true );
            }

            tx.success();
        }

        // when
        try ( Transaction tx = db.beginTx() )
        {
            db.schema().constraintFor( LABEL ).assertPropertyIsUnique( "prop" ).create();
            fail( "Should have failed with ConstraintViolationException" );
            tx.success();
        }
        catch ( ConstraintViolationException ignored )
        {
        }

        // then
        try ( Transaction ignore = db.beginTx() )
        {
            assertEquals( 0, Iterables.count( db.schema().getIndexes() ) );
        }
    }
}
