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
import scala.collection.mutable.Buffer
import scala.collection.JavaConverters._
import scala.collection.mutable.StringBuilder

object JavaCollectionUtils {



  trait JavaCollectionMaker[CC[X] <: Collection[X]] {
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

  implicit object VectorMaker extends JavaCollectionMaker[java.util.Vector] {
    def mkEmptyCollection[A]: java.util.Vector[A] = new java.util.Vector[A]
  }

  implicit object StackMaker extends JavaCollectionMaker[java.util.Stack] {
    def mkEmptyCollection[A]: java.util.Stack[A] = new java.util.Stack[A]
  }

  // Set defaults to HashSet
  implicit object SetMaker extends JavaCollectionMaker[java.util.Set] {
    def mkEmptyCollection[A]: java.util.Set[A] = new java.util.HashSet[A]
  }

  implicit object HashSetMaker extends JavaCollectionMaker[java.util.HashSet] {
    def mkEmptyCollection[A]: java.util.HashSet[A] = new java.util.HashSet[A]
  }

  implicit object LinkedHashSetMaker extends JavaCollectionMaker[java.util.LinkedHashSet] {
    def mkEmptyCollection[A]: java.util.LinkedHashSet[A] = new java.util.LinkedHashSet[A]
  }

  implicit object TreeSetMaker extends JavaCollectionMaker[java.util.TreeSet] {
    def mkEmptyCollection[A]: java.util.TreeSet[A] = new java.util.TreeSet[A]
  }



  trait JavaMapMaker[M[X, Y] <: java.util.Map[X, Y]] {
    def mkEmptyMap[K, V]: M[K, V]
  }

  // Map defaults to HashMap
  implicit object MapMaker extends JavaMapMaker[java.util.Map] {
    def mkEmptyMap[K, V]: java.util.Map[K, V] = new java.util.HashMap[K, V]
  }

  implicit object HashMapMaker extends JavaMapMaker[java.util.HashMap] {
    def mkEmptyMap[K, V]: java.util.HashMap[K, V] = new java.util.HashMap[K, V]
  }

  implicit object LinkedHashMapMaker extends JavaMapMaker[java.util.LinkedHashMap] {
    def mkEmptyMap[K, V]: java.util.LinkedHashMap[K, V] = new java.util.LinkedHashMap[K, V]
  }

  implicit object TreeMapMaker extends JavaMapMaker[java.util.TreeMap] {
    def mkEmptyMap[K, V]: java.util.TreeMap[K, V] = new java.util.TreeMap[K, V]
  }



  implicit class JavaCollectionOps[A, CC[X] <: Collection[X]](val collection: CC[A]) extends AnyVal {

    def toBuffer: Buffer[A] = collection.asScala.toBuffer

    def toIndexedSeq: IndexedSeq[A] = collection.asScala.toIndexedSeq

    def toIterable: Iterable[A] = collection.asScala

    def toIterator: Iterator[A] = collection.asScala.toIterator

    def toList: List[A] = collection.asScala.toList

    def toSeq: Seq[A] = collection.asScala.toSeq

    def toSet: Set[A] = collection.asScala.toSet

    def toStream: Stream[A] = collection.asScala.toStream

    def toVector: Vector[A] = collection.asScala.toVector

    def nonEmpty: Boolean = !collection.isEmpty

    def mkString: String = mkString("")

    def mkString(sep: String): String = {
      if (collection.isEmpty) return ""
      val it = collection.iterator()
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
      val cc = maker.mkEmptyCollection[B]
      while (it.hasNext()) {
        cc.add(f(it.next()))
      }
      cc
    }

    def flatMap[B](f: A => CC[B])(implicit maker: JavaCollectionMaker[CC]): CC[B] = {
      val it = collection.iterator()
      val cc = maker.mkEmptyCollection[B]
      while (it.hasNext()) {
        cc.addAll(f(it.next()))
      }
      cc
    }

    def filter(p: A => Boolean)(implicit maker: JavaCollectionMaker[CC]): CC[A] = {
      val it = collection.iterator()
      val cc = maker.mkEmptyCollection[A]
      while (it.hasNext()) {
        val x = it.next()
        if (p(x)) cc.add(x)
      }
      cc
    }

    def filterNot(p: A => Boolean)(implicit maker: JavaCollectionMaker[CC]): CC[A] = {
      filter(x => !p(x))
    }

  }



  implicit class JavaMapWrapper[K, V, M[X, Y] <: java.util.Map[X, Y]](val map: M[K, V]) extends AnyVal {

    def toMap: Map[K, V] = map.asScala.toMap

    def nonEmpty: Boolean = !map.isEmpty

    def mkString: String = mkString("")

    def mkString(sep: String): String = {
      map.entrySet().map(e => s"${e.getKey()} -> ${e.getValue()}").mkString(sep)
    }

    def foreach(f: ((K, V)) => Unit): Unit = {
      for (e <- map.entrySet()) {
        f((e.getKey(), e.getValue()))
      }
    }

    def map[A, B](f: ((K, V)) => (A, B))(implicit maker: JavaMapMaker[M]): M[A, B] = {
      val m = maker.mkEmptyMap[A, B]
      for (e <- map.entrySet()) {
        val (k, v) = f((e.getKey(), e.getValue()))
        m.put(k, v)
        ()
      }
      m
    }

    def flatMap[A, B](f: ((K, V)) => M[A, B])(implicit maker: JavaMapMaker[M]): M[A, B] = {
      val m = maker.mkEmptyMap[A, B]
      for (e <- map.entrySet()) {
        m.putAll(f((e.getKey(), e.getValue())))
      }
      m
    }

    def filter(p: ((K, V)) => Boolean)(implicit maker: JavaMapMaker[M]): M[K, V] = {
      val m = maker.mkEmptyMap[K, V]
      for (e <- map.entrySet()) {
        val k = e.getKey()
        val v = e.getValue()
        if (p((k, v))) {
          m.put(k, v)
          ()
        }
      }
      m
    }

    def filterNot(p: ((K, V)) => Boolean)(implicit maker: JavaMapMaker[M]): M[K, V] = {
      filter(e => !p(e))
    }

  }

}
