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
package org.neo4j.cypher.internal.runtime.interpreted

import java.net.URL

import org.eclipse.collections.api.iterator.LongIterator
import org.neo4j.cypher.internal.planner.v3_5.spi.{IdempotentResult, IndexDescriptor}
import org.neo4j.cypher.internal.runtime._
import org.neo4j.cypher.internal.v3_5.expressions.SemanticDirection
import org.neo4j.cypher.internal.v3_5.logical.plans.{IndexOrder, QualifiedName}
import org.neo4j.graphdb.{Path, PropertyContainer}
import org.neo4j.internal.kernel.api.helpers.RelationshipSelectionCursor
import org.neo4j.internal.kernel.api.procs.ProcedureCallContext
import org.neo4j.internal.kernel.api.{IndexQuery, IndexReference, NodeValueIndexCursor}
import org.neo4j.kernel.impl.api.store.RelationshipIterator
import org.neo4j.kernel.impl.core.EmbeddedProxySPI
import org.neo4j.values.AnyValue
import org.neo4j.values.storable.TextValue
import org.neo4j.values.virtual.{ListValue, MapValue, NodeValue, RelationshipValue}

trait QueryContextAdaptation {
  self: QueryContext =>

  override def createNewQueryContext(): QueryContext = ???

  override def indexSeekByContains[RESULT](index: IndexReference,
                                           needsValues: Boolean,
                                           indexOrder: IndexOrder,
                                           value: TextValue): NodeValueIndexCursor = ???

  override def indexSeekByEndsWith[RESULT](index: IndexReference,
                                           needsValues: Boolean,
                                           indexOrder: IndexOrder,
                                           value: TextValue): NodeValueIndexCursor = ???

  override def createNodePropertyExistenceConstraint(labelId: Int, propertyKeyId: Int): Boolean = ???

  override def createNode(labels: Array[Int]): NodeValue = ???

  override def createNodeId(labels: Array[Int]): Long = ???

  override def dropRelationshipPropertyExistenceConstraint(relTypeId: Int, propertyKeyId: Int): Unit = ???

  override def createUniqueConstraint(descriptor: IndexDescriptor): Boolean = ???

  override def createNodeKeyConstraint(descriptor: IndexDescriptor): Boolean = ???

  override def getOrCreateRelTypeId(relTypeName: String): Int = ???

  override def dropNodePropertyExistenceConstraint(labelId: Int, propertyKeyId: Int): Unit = ???

  override def singleShortestPath(left: Long, right: Long, depth: Int, expander: Expander, pathPredicate: KernelPredicate[Path], filters: Seq[KernelPredicate[PropertyContainer]]): Option[Path] = ???

  override def asObject(value: AnyValue): AnyRef = ???

  override def relationshipGetStartNode(relationship: RelationshipValue): NodeValue = ???

  override def relationshipGetEndNode(relationship: RelationshipValue): NodeValue = ???

  /**
    * This should not be used. We'll remove sooner (or later). Don't do it.
    */
  override def withAnyOpenQueryContext[T](work: (QueryContext) => T): T = ???

  // Legacy dependency between kernel and compiler
  override def variableLengthPathExpand(realNode: Long, minHops: Option[Int], maxHops: Option[Int], direction: SemanticDirection, relTypes: Seq[String]): scala.Iterator[Path] = ???

  override def nodeGetDegree(node: Long, dir: SemanticDirection): Int = ???

  override def nodeGetDegree(node: Long, dir: SemanticDirection, relTypeId: Int): Int = ???

  override def entityAccessor: EmbeddedProxySPI = ???

  override def withActiveRead: QueryContext = ???

  override def resources: ResourceManager = ???

  override def getOrCreatePropertyKeyId(propertyKey: String): Int = ???

  override def getOrCreatePropertyKeyIds(propertyKeys: Array[String]): Array[Int] = ???

  override def isLabelSetOnNode(label: Int, node: Long): Boolean = ???

  override def indexReference(label: Int, properties: Int*): IndexReference = ???

  override def indexSeek[RESULT](index: IndexReference,
                                 needsValues: Boolean,
                                 indexOrder: IndexOrder,
                                 values: Seq[IndexQuery]): NodeValueIndexCursor = ???

  override def getRelationshipsForIds(node: Long, dir: SemanticDirection, types: Option[Array[Int]]): scala.Iterator[RelationshipValue] = ???

  override def getRelationshipsForIdsPrimitive(node: Long, dir: SemanticDirection, types: Option[Array[Int]]): RelationshipIterator = ???

  override def nodeAsMap(id: Long): MapValue = ???

  override def relationshipAsMap(id: Long): MapValue = ???

  override def getRelationshipsCursor(node: Long, dir: SemanticDirection, types: Option[Array[Int]]): RelationshipSelectionCursor = ???

  override def getRelationshipFor(relationshipId: Long, typeId: Int, startNodeId: Long, endNodeId: Long): RelationshipValue = ???

  override def getLabelsForNode(node: Long): ListValue = ???

  override def dropUniqueConstraint(descriptor: IndexDescriptor): Unit = ???

  override def dropNodeKeyConstraint(descriptor: IndexDescriptor): Unit = ???

  override def transactionalContext: QueryTransactionalContext = ???

  override def allShortestPath(left: Long, right: Long, depth: Int, expander: Expander, pathPredicate: KernelPredicate[Path], filters: Seq[KernelPredicate[PropertyContainer]]): scala.Iterator[Path] = ???

  override def nodeOps: Operations[NodeValue] = ???

  override def lockRelationships(relIds: Long*): Unit = ???

  override def getOrCreateLabelId(labelName: String): Int = ???

  override def indexScan[RESULT <: AnyRef](index: IndexReference,
                                           needsValues: Boolean,
                                           indexOrder: IndexOrder): NodeValueIndexCursor = ???

  override def getImportURL(url: URL): Either[String, URL] = ???

  override def relationshipCountByCountStore(startLabelId: Int, typeId: Int, endLabelId: Int): Long = ???

  override def nodeIsDense(node: Long): Boolean = ???

  override def setLabelsOnNode(node: Long, labelIds: scala.Iterator[Int]): Int = ???

  override def createRelationshipPropertyExistenceConstraint(relTypeId: Int, propertyKeyId: Int): Boolean = ???

  override def dropIndexRule(descriptor: IndexDescriptor): Unit = ???

  override def lockNodes(nodeIds: Long*): Unit = ???

  override def relationshipOps: Operations[RelationshipValue] = ???

  override def getNodesByLabel(id: Int): scala.Iterator[NodeValue] = ???

  override def getNodesByLabelPrimitive(id: Int): LongIterator = ???

  override def lockingUniqueIndexSeek[RESULT](index: IndexReference,
                                              values: Seq[IndexQuery.ExactPredicate]): NodeValueIndexCursor = ???

  override def callReadOnlyProcedure(id: Int, args: Seq[Any], allowed: Array[String], procedureCallContext: ProcedureCallContext): scala.Iterator[Array[AnyRef]] = ???

  override def callReadWriteProcedure(id: Int, args: Seq[Any], allowed: Array[String], procedureCallContext: ProcedureCallContext): scala.Iterator[Array[AnyRef]] = ???

  override def callSchemaWriteProcedure(id: Int, args: Seq[Any], allowed: Array[String], procedureCallContext: ProcedureCallContext): Iterator[Array[AnyRef]] = ???

  override def callDbmsProcedure(id: Int, args: Seq[Any], allowed: Array[String], procedureCallContext: ProcedureCallContext): Iterator[Array[AnyRef]] = ???

  override def callReadOnlyProcedure(name: QualifiedName, args: Seq[Any], allowed: Array[String], procedureCallContext: ProcedureCallContext): scala.Iterator[Array[AnyRef]] = ???

  override def callReadWriteProcedure(name: QualifiedName, args: Seq[Any], allowed: Array[String], procedureCallContext: ProcedureCallContext): scala.Iterator[Array[AnyRef]] = ???

  override def callSchemaWriteProcedure(name: QualifiedName, args: Seq[Any], allowed: Array[String], procedureCallContext: ProcedureCallContext): Iterator[Array[AnyRef]] = ???

  override def callDbmsProcedure(name: QualifiedName, args: Seq[Any], allowed: Array[String], procedureCallContext: ProcedureCallContext): Iterator[Array[AnyRef]] = ???

  override def callFunction(id: Int, args: Seq[AnyValue], allowed: Array[String]): AnyValue = ???

  override def aggregateFunction(id: Int,
                                 allowed: Array[String]): UserDefinedAggregator = ???

  override def callFunction(name: QualifiedName, args: Seq[AnyValue], allowed: Array[String]): AnyValue = ???

  override def aggregateFunction(name: QualifiedName, allowed: Array[String]): UserDefinedAggregator = ???

  override def removeLabelsFromNode(node: Long, labelIds: scala.Iterator[Int]): Int = ???

  override def createRelationship(start: Long, end: Long, relType: Int): RelationshipValue = ???

  override def nodeCountByCountStore(labelId: Int): Long = ???

  override def addIndexRule(descriptor: IndexDescriptor): IdempotentResult[IndexReference] = ???

  override def getOptRelTypeId(relType: String): Option[Int] = ???

  override def getRelTypeName(id: Int): String = ???

  override def getRelTypeId(relType: String): Int = ???

  override def getOptPropertyKeyId(propertyKeyName: String): Option[Int] = ???

  override def getLabelName(id: Int): String = ???

  override def getOptLabelId(labelName: String): Option[Int] = ???

  override def getPropertyKeyId(propertyKeyName: String): Int = ???

  override def getPropertyKeyName(id: Int): String = ???

  override def getLabelId(labelName: String): Int = ???

  override def detachDeleteNode(node: Long): Int = ???

  override def assertSchemaWritesAllowed(): Unit = ???

  override def nodeGetOutgoingDegree(node: Long): Int = ???

  override def nodeGetOutgoingDegree(node: Long, relationship: Int): Int = ???

  override def nodeGetIncomingDegree(node: Long): Int = ???

  override def nodeGetIncomingDegree(node: Long, relationship: Int): Int = ???

  override def nodeGetTotalDegree(node: Long): Int = ???

  override def nodeGetTotalDegree(node: Long, relationship: Int): Int = ???
}
