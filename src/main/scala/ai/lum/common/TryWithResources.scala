/*
 * Copyright 2019 lum.ai
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

import scala.util.control.NonFatal
import scala.language.reflectiveCalls

object TryWithResources {

  type Closeable = { def close(): Unit }

  /** like java's try-with-resources */
  def using[A <: Closeable, B](resource: A)(f: A => B): B = {
    var exception: Option[Throwable] = None
    try {
      // try to call f() with the resource as argument
      f(resource)
    } catch {
      case e: Throwable =>
        // if there is an exception
        // then save it for later and rethrow it
        exception = Some(e)
        throw e
    } finally {
      exception match {
        case None =>
          // if there was no exception
          // then we can just close the resource
          // and if there is an exception during closing
          // we can just let it happen
          resource.close()
        case Some(e1) =>
          // if there was an exception
          // then we need to be more careful when closing the resource
          try {
            resource.close()
          } catch {
            case NonFatal(e2) =>
              // if we get a nonfatal exception during closing
              // then we need to suppress it
              e1.addSuppressed(e2)
            case e2: Throwable if NonFatal(e1) =>
              // if we get a fatal exception during closing
              // and the original exception wasn't fatal
              // then we need to suppress the original exception
              // and throw the new one
              e2.addSuppressed(e1)
              throw e2
            case e2: Throwable =>
              // if we get a fatal exception during closing
              // and the original exception was fatal too
              // then we suppress the new exception
              e1.addSuppressed(e2)
          }
      }
    }
  }

}
