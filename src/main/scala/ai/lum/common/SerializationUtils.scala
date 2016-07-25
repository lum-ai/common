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

object SerializationUtils {

  def roundtrip[A](obj: A): A = deserialize(serialize(obj))

  def serialize[A](obj: A): Array[Byte] = {
    val baos = new ByteArrayOutputStream
    serialize(obj, baos)
    baos.toByteArray
  }

  def serialize[A](obj: A, file: File): Unit = {
    serialize(obj, new FileOutputStream(file))
  }

  def serialize[A](obj: A, filename: String): Unit = {
    serialize(obj, new FileOutputStream(filename))
  }

  def serialize[A](obj: A, outputStream: OutputStream): Unit = {
    val oos = new ObjectOutputStream(outputStream)
    oos.writeObject(obj)
    oos.close()
  }

  def deserialize[A](data: Array[Byte]): A = {
    deserialize(new ByteArrayInputStream(data))
  }

  def deserialize[A](file: File): A = {
    deserialize(new FileInputStream(file))
  }

  def deserialize[A](filename: String): A = {
    deserialize(new FileInputStream(filename))
  }

  def deserialize[A](inputStream: InputStream): A = {
    val classLoader = getClass().getClassLoader()
    val ois = new ClassLoaderObjectInputStream(classLoader, inputStream)
    val obj = ois.readObject().asInstanceOf[A]
    ois.close()
    obj
  }

}
