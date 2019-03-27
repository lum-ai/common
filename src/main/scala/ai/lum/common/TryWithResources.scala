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
    try {
      f(resource)
    } catch {
      case e1: Throwable =>
        try {
          resource.close()
        } catch {
          case NonFatal(e2) =>
            e1.addSuppressed(e2)
          case e2: Throwable =>
            e2.addSuppressed(e1)
            throw e2
        }
        throw e1
    } finally {
      resource.close()
    }
  }

}
