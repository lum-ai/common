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

import java.io._
import scala.language.reflectiveCalls
import org.apache.commons.io.input.ClassLoaderObjectInputStream

object Serializer {

  type Closable = { def close(): Unit }

  def using[A <: Closable, B](resource: A)(f: A => B): B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }

  /** serialize object to output stream */
  def save[A](obj: A, outputStream: OutputStream): Unit = {
    using(new ObjectOutputStream(outputStream)) { oos =>
      oos.writeObject(obj)
    }
  }

  /** serialize object to file */
  def save[A](obj: A, file: File): Unit = {
    using(new FileOutputStream(file)) { fos =>
      save(obj, fos)
    }
  }

  /** serialize object to file */
  def save[A](obj: A, filename: String): Unit = {
    using(new FileOutputStream(filename)) { fos =>
      save(obj, fos)
    }
  }

  /** serialize object to byte array */
  def save[A](obj: A): Array[Byte] = {
    using(new ByteArrayOutputStream()) { baos =>
      save(obj, baos)
      baos.toByteArray
    }
  }

  /** deserialize from input stream */
  def load[A](inputStream: InputStream): A = {
    load[A](inputStream, getClass().getClassLoader())
  }

  /** deserialize from input stream */
  def load[A](inputStream: InputStream, classLoader: ClassLoader): A = {
    using(new ClassLoaderObjectInputStream(classLoader, inputStream)) { ois =>
      ois.readObject().asInstanceOf[A]
    }
  }

  /** deserialize from file */
  def load[A](file: File): A = {
    load[A](file, getClass().getClassLoader())
  }

  /** deserialize from file */
  def load[A](file: File, classLoader: ClassLoader): A = {
    using(new FileInputStream(file)) { fis =>
      load[A](fis, classLoader)
    }
  }

  /** deserialize from file */
  def load[A](filename: String): A = {
    load[A](filename, getClass().getClassLoader())
  }

  /** deserialize from file */
  def load[A](filename: String, classLoader: ClassLoader): A = {
    using(new FileInputStream(filename)) { fis =>
      load[A](fis, classLoader)
    }
  }

  /** deserialize from byte array */
  def load[A](bytes: Array[Byte]): A = {
    load[A](bytes, getClass().getClassLoader())
  }

  /** deserialize from byte array */
  def load[A](bytes: Array[Byte], classLoader: ClassLoader): A = {
    using(new ByteArrayInputStream(bytes)) { bais =>
      load[A](bais, classLoader)
    }
  }

}
