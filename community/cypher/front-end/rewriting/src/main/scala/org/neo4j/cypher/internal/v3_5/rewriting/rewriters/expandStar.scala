/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.cypher.internal.v3_5.rewriting.rewriters

import org.neo4j.cypher.internal.v3_5.ast._
import org.neo4j.cypher.internal.v3_5.ast.semantics.SemanticState
import org.neo4j.cypher.internal.v3_5.expressions.{Expression, Variable}
import org.neo4j.cypher.internal.v3_5.util.{Rewriter, bottomUp}

case class expandStar(state: SemanticState) extends Rewriter {

  def apply(that: AnyRef): AnyRef = instance(that)

  private val rewriter = Rewriter.lift {
    case clause@With(_, values, _, _, _, _) if values.includeExisting =>
      val newReturnItems = if (values.includeExisting) returnItems(clause, values.items) else values
      clause.copy(returnItems = newReturnItems)(clause.position)

    case clause@Return(_, values, _, _, _, excludedNames) if values.includeExisting =>
      val newReturnItems = if (values.includeExisting) returnItems(clause, values.items, excludedNames) else values
      clause.copy(returnItems = newReturnItems, excludedNames = Set.empty)(clause.position)

    case expandedAstNode =>
      expandedAstNode
  }

  private val instance = bottomUp(rewriter, _.isInstanceOf[Expression])

  private def returnItems(clause: Clause, listedItems: Seq[ReturnItem], excludedNames: Set[String] = Set.empty)
  : ReturnItemsDef = {
    val scope = state.scope(clause).getOrElse {
      throw new IllegalStateException(s"${clause.name} should note its Scope in the SemanticState")
    }

    val clausePos = clause.position
    val symbolNames = scope.symbolNames -- excludedNames
    val expandedItems = symbolNames.toIndexedSeq.sorted.map { id =>
      val idPos = scope.symbolTable(id).definition.position
      val expr = Variable(id)(idPos)
      val alias = expr.copyId
      AliasedReturnItem(expr, alias)(clausePos)
    }

    val newItems = expandedItems ++ listedItems
    ReturnItems(includeExisting = false, newItems)(clausePos)
  }
}
