// Copyright (C) 2018 Don Kelly <karfai@gmail.com>
// Copyright (C) 2018 Hayk Pilosyan <hayk.pilos@gmail.com>

// This file is part of Interlibr, a functional component of an
// Internet of Rules (IoR).

// ACKNOWLEDGEMENTS
// Funds: Xalgorithms Foundation
// Collaborators: Don Kelly, Joseph Potvin and Bill Olders.

// This program is free software: you can redistribute it and/or
// modify it under the terms of the GNU Affero General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Affero General Public License for more details.

// You should have received a copy of the GNU Affero General Public
// License along with this program. If not, see
// <http://www.gnu.org/licenses/>.
package org.xalgorithms.rules.elements

import com.github.javafaker.Faker
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import play.api.libs.json._

import org.xalgorithms.rules._
import org.xalgorithms.rules.elements._

class ContextSpec extends FlatSpec with Matchers with MockFactory {
  val faker = new Faker()

  "GlobalContext" should "record revisions to a table" in {
    val ctx = new GlobalContext(null)
    val tables = (0 to faker.number().numberBetween(2, 10)).map { i =>
      new TableReference(s"table${i}")
    }

    tables.foreach { table => ctx.revise_table(table, new Revision(Seq())) }
    tables.foreach { table => ctx.revise_table(table, new Revision(Seq())) }

    val revs = ctx.revisions()
    revs.size shouldEqual(tables.size)
    tables.foreach { table =>
      revs(table).size shouldEqual(2)
    }
  }

  "RowContext" should "delegate to the contained Context" in {
    // val ctx = mock[Context]
    // val secs = mock[Sections]
    // val rctx = new RowContext(ctx, Map(), Map())

    // (ctx.sections _).expects.returning(secs)
    // rctx.sections shouldEqual(secs)

    // val section = "section"
    // val m = Map("a" -> new StringValue("00"), "b" -> new StringValue("01"))
    // val tbl = Seq(m)
    // val tbl_key = "table0"
    // val map_key = "a.b.c"
    // val map_val = new StringValue("map_val")

    // (secs.retain_values _).expects(section, m).once
    // rctx.sections.retain_values(section, m)

    // (ctx.retain_table _).expects(section, tbl_key, tbl)
    // rctx.retain_table(section, tbl_key, tbl)

    // (ctx.lookup_in_map _).expects(section, map_key).returning(Some(map_val))
    // val mov = rctx.lookup_in_map(section, map_key)
    // mov match {
    //   case Some(mv) => {
    //     mv shouldBe a [StringValue]
    //     mv.asInstanceOf[StringValue].value shouldEqual(map_val.value)
    //   }
    //   case None => true shouldEqual(false)
    // }

    // (ctx.lookup_table _).expects(section, tbl_key).returning(tbl)
    // val t = rctx.lookup_table(section, tbl_key)
    // t.length shouldEqual(1)
    // t(0)("a") shouldBe a [StringValue]
    // t(0)("a").asInstanceOf[StringValue].value shouldEqual(m("a").value)

    // val revisions = (0 to faker.number().numberBetween(2, 10)).map { i =>
    //   Tuple2(new TableReference(s"table${i}"), Seq(new Revision(Seq())))
    // }.toMap

    // (ctx.revisions _).expects().returning(revisions)

    // rctx.revisions() shouldEqual(revisions)
  }

  // it should "allow local modification without affecting the original source" in {
  //   val ctx = mock[Context]
  //   val keys = Seq("a", "b", "c")
  //   val new_keys = Seq("d", "e")
  //   val original = keys.map { k => (k, new StringValue(k)) }.toMap
  //   val original_size = original.size
  //   val rctx = new RowContext(ctx, original, Map())

  //   new_keys.foreach { k =>
  //     rctx.update_local(Map(k -> new StringValue(k)))

  //     original.size shouldEqual(original_size)
  //     original.contains(k) shouldEqual(false)

  //     val vo = rctx.lookup_in_map("_local", k)
  //     vo match {
  //       case Some(v) => {
  //         v shouldBe a [StringValue]
  //         v.asInstanceOf[StringValue].value shouldEqual(k)
  //       }
  //       case None => true shouldBe false
  //     }
  //   }
  // }

  it should "serialize into JSON" in {
    val ctx = new GlobalContext(null)
    val values0 = Map("A" -> 100.0, "B" -> 333.0)
    val table0 = Seq(
        Map("A" -> 2.0, "B" -> 3.0),
        Map("A" -> 4.0, "B" -> 6.0)
    )

    val expected = Json.obj(
      "values" -> Json.obj(
        "values0" -> values0,
      ),
      "tables" -> Json.obj("table0" -> table0),
    )

    ctx.sections.tables.retain("table0", table0.map { r => r.mapValues(new NumberValue(_)) })
    ctx.sections.retain_values("values0", values0.mapValues(new NumberValue(_)))

    ctx.serialize shouldEqual(expected)
  }
}
