package ai.lum.common

import scala.collection.{ AbstractIterator, GenTraversableOnce }
import scala.collection.parallel.{ ParSeq, TaskSupport, defaultTaskSupport }

object IteratorUtils {

  implicit class IteratorWrapper[A](val iterator: Iterator[A]) extends AnyVal {
    def par: ParIterator[A] = par(100)
    def par(n: Int): ParIterator[A] = new ParIterator(iterator.grouped(n))
  }

  // Gets an iterator of groups of A.
  // Each group is traversed in parallel.
  class ParIterator[A](
      private val remainingGroups: Iterator[Seq[A]]
  ) extends AbstractIterator[A] { self =>

    var tasksupport: TaskSupport = defaultTaskSupport

    private var currentGroup: List[A] = Nil

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

    // parallelizes a single group and attaches tasksupport
    private def mkPar(group: Seq[A]): ParSeq[A] = {
      val par = group.par
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
      allGroups.foreach(g => mkPar(g).foreach(f))
    }

    override def map[B](f: A => B): Iterator[B] = {
      val iter = allGroups.map(g => mkPar(g).map(f).seq)
      val parIter = new ParIterator(iter)
      parIter.tasksupport = tasksupport
      parIter
    }

    override def flatMap[B](f: A => GenTraversableOnce[B]): Iterator[B] = {
      val iter = allGroups.map(g => mkPar(g).flatMap(f).seq).filter(_.nonEmpty)
      val parIter = new ParIterator(iter)
      parIter.tasksupport = tasksupport
      parIter
    }

    override def filter(p: A => Boolean): Iterator[A] = {
      val iter = allGroups.map(g => mkPar(g).filter(p).seq).filter(_.nonEmpty)
      val parIter = new ParIterator(iter)
      parIter.tasksupport = tasksupport
      parIter
    }

  }

}
