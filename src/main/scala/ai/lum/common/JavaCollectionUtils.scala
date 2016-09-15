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
import scala.language.higherKinds
import scala.collection.JavaConverters._
import scala.collection.mutable.StringBuilder

object JavaCollectionUtils {

  implicit class JavaCollectionOps[A, CC[A] <: Collection[A]](val collection: CC[A]) extends AnyVal {

    def toIterable: Iterable[A] = collection.asScala

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

    def map[B](f: A => B)(implicit maker: JavaCollectionMaker[CC]): CC[B] = {
      val it = collection.iterator()
      val col = maker.mkEmptyCollection[B]
      while (it.hasNext()) {
        col.add(f(it.next()))
      }
      col
    }

    def flatMap[B](f: A => CC[B])(implicit maker: JavaCollectionMaker[CC]): CC[B] = {
      val it = collection.iterator()
      val col = maker.mkEmptyCollection[B]
      while (it.hasNext()) {
        col.addAll(f(it.next()))
      }
      col
    }

    def filter(p: A => Boolean)(implicit maker: JavaCollectionMaker[CC]): CC[A] = {
      val it = collection.iterator()
      val col = maker.mkEmptyCollection[A]
      while (it.hasNext()) {
        val r = it.next()
        if (p(r)) col.add(r)
      }
      col
    }

  }

  trait JavaCollectionMaker[CC[_] <: Collection[_]] {
    def mkEmptyCollection[A]: CC[A]
  }

  // List defaults to ArrayList
  implicit object ListMaker extends JavaCollectionMaker[java.util.List] {
    def mkEmptyCollection[A]: java.util.List[A] = new java.util.ArrayList[A]
  }

  implicit object ArrayListMaker extends JavaCollectionMaker[java.util.ArrayList] {
    def mkEmptyCollection[A]: java.util.ArrayList[A] = new java.util.ArrayList[A]
  }

  implicit object LinkedListMaker extends JavaCollectionMaker[java.util.LinkedList] {
    def mkEmptyCollection[A]: java.util.LinkedList[A] = new java.util.LinkedList[A]
  }

  implicit object StackMaker extends JavaCollectionMaker[java.util.Stack] {
    def mkEmptyCollection[A]: java.util.Stack[A] = new java.util.Stack[A]
  }

}
