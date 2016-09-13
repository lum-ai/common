/*
 * Copyright 2016 lum.ai
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

package ai.lum.common

import java.util.Collection
import scala.collection.mutable.StringBuilder

object CollectionUtils {

  implicit class CollectionOps[A](val collection: Collection[A]) extends AnyVal {

    def mkString: String = mkString("")

    def mkString(sep: String): String = {
      val it = collection.iterator()
      if (!it.hasNext()) return ""
      val builder = new StringBuilder
      builder ++= it.next().toString()
      while (it.hasNext()) {
        builder ++= sep
        builder ++= it.next().toString()
      }
      builder.toString()
    }

    def foreach(f: A => Unit): Unit = {
      val it = collection.iterator()
      while (it.hasNext()) {
        f(it.next())
      }
    }

    def map[B](f: A => B): Collection[B] = {
      val rs = mkEmptyCollection[B]
      val it = collection.iterator()
      while (it.hasNext()) {
        rs.add(f(it.next()))
      }
      rs
    }

    def flatMap[B](f: A => Collection[B]): Collection[B] = {
      val rs = mkEmptyCollection[B]
      val it = collection.iterator()
      while (it.hasNext()) {
        rs.addAll(f(it.next()))
      }
      rs
    }

    def filter(p: A => Boolean): Collection[A] = {
      val rs = mkEmptyCollection[A]
      val it = collection.iterator()
      while (it.hasNext()) {
        val r = it.next()
        if (p(r)) rs.add(r)
      }
      rs
    }

    private def mkEmptyCollection[B]: Collection[B] = collection match {
      case c: java.util.ArrayList[A] => new java.util.ArrayList[B]
    }

  }

}
