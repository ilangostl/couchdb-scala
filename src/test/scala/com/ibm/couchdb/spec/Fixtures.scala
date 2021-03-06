/*
 * Copyright 2015 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.couchdb.spec

import com.ibm.couchdb.Lenses._
import com.ibm.couchdb._
import monocle.macros.Lenser

trait Fixtures {

  case class FixPerson(name: String, age: Int)

  val typeMapping = TypeMapping(classOf[FixPerson] -> "Person")

  val lenser         = Lenser[FixPerson]
  val _personName    = lenser(_.name)
  val _personAge     = lenser(_.age)
  val _docPersonName = _couchDoc composeLens _personName
  val _docPersonAge  = _couchDoc composeLens _personAge

  val fixAlice = FixPerson("Alice", 25)
  val fixBob   = FixPerson("Bob", 30)
  val fixCarl  = FixPerson("Carl", 20)

  object FixViews {
    val names    = "names"
    val compound = "compound"
  }

  object FixShows {
    val csv = "csv"
  }

  object FixLists {
    val csvAll = "csv-all"
  }

  val fixDesign = CouchDesign(
    name = "test-design",

    views = Map(
      FixViews.names → CouchView(map =
        """
          |function(doc) {
          | emit(doc.doc.name, doc.doc.name);
          |}
        """.stripMargin),
      FixViews.compound → CouchView(map =
        """
          |function(doc) {
          | var d = doc.doc;
          | emit([d.age, d.name], d);
          |}
        """.stripMargin)),

    shows = Map(
      FixShows.csv →
        """
          |function(doc, req) {
          | if (doc !== null && doc.kind == "Person") {
          |   var res = doc.doc.name + ',' + doc.doc.age;
          |   if (typeof req.query.extra !== "undefined") {
          |     res += ',' + req.query.extra;
          |   }
          |   return res;
          | } else {
          |   return 'empty show';
          | }
          |}
        """.stripMargin),

    lists = Map(
      FixLists.csvAll →
        """
          |function(head, req) {
          | var row = getRow();
          | if (!row) {
          |  return 'no rows';
          | }
          | if (typeof req.query.header !== "undefined" && req.query.header) {
          |  send('name,age\n');
          | }
          | send(row.value.name + ',' + row.value.age + '\n');
          | while (row = getRow()) {
          |   send(row.value.name + ',' + row.value.age + '\n');
          | }
          |}
        """.stripMargin)

  )

  val fixAttachmentName        = "attachment"
  val fixAttachmentData        = Array[Byte](-1, 0, 1, 2, 3)
  val fixAttachmentContentType = "image/jpg"

}
