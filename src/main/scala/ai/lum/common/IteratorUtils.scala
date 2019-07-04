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

import scala.collection.{ AbstractIterator, GenTraversableOnce }
import scala.collection.parallel.{ ParSeq, TaskSupport, defaultTaskSupport }

object IteratorUtils {

  implicit class LumAICommonIteratorWrapper[A](val iterator: Iterator[A]) extends AnyVal {
    def par: ParIterator[A] = par(100)
    def par(n: Int): ParIterator[A] = new ParIterator(iterator.grouped(n))
  }

  // Gets an iterator of groups of A.
  // Each group is traversed in parallel.
  class ParIterator[A](groups: Iterator[Seq[A]]) extends AbstractIterator[A] { self =>

    var tasksupport: TaskSupport = defaultTaskSupport

    private var currentGroup: List[A] = Nil
    private val remainingGroups: Iterator[Seq[A]] = groups.filter(_.nonEmpty)

    def next(): A = currentGroup match {
      case head :: tail =>
        // return next element in current group
        currentGroup = tail
        head
      case Nil =>
        // get next group and try again
        currentGroup = remainingGroups.next().toList
        next()
    }

    def hasNext: Boolean = currentGroup.nonEmpty || remainingGroups.hasNext

    // returns sequential version of this iterator
    override def seq: Iterator[A] = new AbstractIterator[A] {
      def next(): A = self.next()
      def hasNext: Boolean = self.hasNext
    }

    // returns parallel version of this iterator
    def par: ParIterator[A] = this

    // parallelizes a single group and attaches tasksupport
    private def mkParSeq[B](group: Seq[B]): ParSeq[B] = {
      val par = group.par
      par.tasksupport = tasksupport
      par
    }

    private def mkParIterator[B](iter: Iterator[Seq[B]]): ParIterator[B] = {
      val par = new ParIterator(iter)
      par.tasksupport = tasksupport
      par
    }

    private def allGroups: Iterator[Seq[A]] = {
      if (currentGroup.isEmpty) {
        remainingGroups
      } else {
        Iterator(currentGroup) ++ remainingGroups
      }
    }

    override def foreach[U](f: A => U): Unit = {
      allGroups.foreach(g => mkParSeq(g).foreach(f))
    }

    override def map[B](f: A => B): Iterator[B] = {
      mkParIterator(allGroups.map(g => mkParSeq(g).map(f).seq))
    }

    override def flatMap[B](f: A => GenTraversableOnce[B]): Iterator[B] = {
      mkParIterator(allGroups.map(g => mkParSeq(g).flatMap(f).seq))
    }

    override def filter(p: A => Boolean): Iterator[A] = {
      mkParIterator(allGroups.map(g => mkParSeq(g).filter(p).seq))
    }

  }

}
