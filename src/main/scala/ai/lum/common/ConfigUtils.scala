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

import java.io.File
import java.time.Duration
import java.net.{ URI, URL }
import java.nio.charset.Charset
import java.nio.file.{ Path, Paths }
import scala.collection.JavaConverters._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import com.typesafe.config._

object ConfigUtils {

  /** Allows to treat a Config object like a Map[A] */
  implicit class ConfigWrapper(val config: Config) extends AnyVal {

    /** Retrieves the value of type A from the Config object.
      * Throws ConfigException.Missing if the value is missing.
      */
    def apply[A: ConfigFieldReader](path: String): A = {
      implicitly[ConfigFieldReader[A]].read(config, path)
    }

    /** Retrieves the value of type A from the Config object.
      * Returns None if the value is missing.
      */
    def get[A: ConfigFieldReader](path: String): Option[A] = {
      try {
        Some(apply[A](path))
      } catch {
        case e: ConfigException.Missing => None
      }
    }

    /** Returns the value of entrySet converted to scala types */
    def entrySetScala: Set[(String, ConfigValue)] = {
      config.entrySet().asScala.map(e => (e.getKey(), e.getValue())).toSet
    }

  }

  /** Reads the value of a Config field according to its type */
  trait ConfigFieldReader[A] {
    def read(config: Config, path: String): A
    def readIterable(config: Config, path: String): Iterable[A]
  }

  object ConfigFieldReader {
    // implicit materializer
    // http://docs.scala-lang.org/overviews/macros/implicits.html#implicit-materializers
    implicit def materialize[T]: ConfigFieldReader[T] = macro materializeImpl[T]
    def materializeImpl[T: c.WeakTypeTag](c: Context): c.Expr[ConfigFieldReader[T]] = {
      import c.universe._
      val T = weakTypeOf[T]
      val CC = T.typeConstructor
      val targs = implicitly[c.WeakTypeTag[T]].tpe match { case TypeRef(_, _, args) => args }
      val A = TypeName(targs.head.toString)
      c.Expr[ConfigFieldReader[T]](q"""
        new ConfigFieldReader[$T] {
          def read(config: Config, path: String): $T = retrieveIterable[$A](config, path).to[$CC]
          def readIterable(config: Config, path: String): Iterable[$T] = ???
          def retrieveIterable[B: ConfigFieldReader](config: Config, path: String): Iterable[B] = {
            implicitly[ConfigFieldReader[B]].readIterable(config, path)
          }
        }
      """)
    }
  }

  implicit object BooleanConfigFieldReader extends ConfigFieldReader[Boolean] {
    def read(config: Config, path: String): Boolean = {
      config.getBoolean(path)
    }
    def readIterable(config: Config, path: String): Iterable[Boolean] = {
      config.getBooleanList(path).asScala.map(Boolean2boolean)
    }
  }

  implicit object IntConfigFieldReader extends ConfigFieldReader[Int] {
    def read(config: Config, path: String): Int = {
      config.getInt(path)
    }
    def readIterable(config: Config, path: String): Iterable[Int] = {
      config.getIntList(path).asScala.map(Integer2int)
    }
  }

  implicit object LongConfigFieldReader extends ConfigFieldReader[Long] {
    def read(config: Config, path: String): Long = {
      config.getLong(path)
    }
    def readIterable(config: Config, path: String): Iterable[Long] = {
      config.getLongList(path).asScala.map(Long2long)
    }
  }

  implicit object DoubleConfigFieldReader extends ConfigFieldReader[Double] {
    def read(config: Config, path: String): Double = {
      config.getDouble(path)
    }
    def readIterable(config: Config, path: String): Iterable[Double] = {
      config.getDoubleList(path).asScala.map(Double2double)
    }
  }

  implicit object StringConfigFieldReader extends ConfigFieldReader[String] {
    def read(config: Config, path: String): String = {
      config.getString(path)
    }
    def readIterable(config: Config, path: String): Iterable[String] = {
      config.getStringList(path).asScala
    }
  }

  implicit object URIConfigFieldReader extends ConfigFieldReader[URI] {
    def read(config: Config, path: String): URI = {
      new URI(config.getString(path))
    }
    def readIterable(config: Config, path: String): Iterable[URI] = {
      config.getStringList(path).asScala.map(new URI(_))
    }
  }

  implicit object URLConfigFieldReader extends ConfigFieldReader[URL] {
    def read(config: Config, path: String): URL = {
      new URL(config.getString(path))
    }
    def readIterable(config: Config, path: String): Iterable[URL] = {
      config.getStringList(path).asScala.map(new URL(_))
    }
  }

  implicit object PathConfigFieldReader extends ConfigFieldReader[Path] {
    def read(config: Config, path: String): Path = {
      Paths.get(config.getString(path))
    }
    def readIterable(config: Config, path: String): Iterable[Path] = {
      config.getStringList(path).asScala.map(Paths.get(_))
    }
  }

  implicit object FileConfigFieldReader extends ConfigFieldReader[File] {
    def read(config: Config, path: String): File = {
      new File(config.getString(path))
    }
    def readIterable(config: Config, path: String): Iterable[File] = {
      config.getStringList(path).asScala.map(new File(_))
    }
  }

  implicit object CharsetConfigFieldReader extends ConfigFieldReader[Charset] {
    def read(config: Config, path: String): Charset = {
      Charset.forName(config.getString(path))
    }
    def readIterable(config: Config, path: String): Iterable[Charset] = {
      config.getStringList(path).asScala.map(Charset.forName)
    }
  }

  implicit object ConfigObjectConfigFieldReader extends ConfigFieldReader[ConfigObject] {
    def read(config: Config, path: String): ConfigObject = {
      config.getObject(path)
    }
    def readIterable(config: Config, path: String): Iterable[ConfigObject] = {
      config.getObjectList(path).asScala
    }
  }

  implicit object ConfigConfigFieldReader extends ConfigFieldReader[Config] {
    def read(config: Config, path: String): Config = {
      config.getConfig(path)
    }
    def readIterable(config: Config, path: String): Iterable[Config] = {
      config.getConfigList(path).asScala
    }
  }

  implicit object ConfigValueConfigFieldReader extends ConfigFieldReader[ConfigValue] {
    def read(config: Config, path: String): ConfigValue = {
      config.getValue(path)
    }
    def readIterable(config: Config, path: String): Iterable[ConfigValue] = ???
  }

  implicit object ConfigMemorySizeConfigFieldReader extends ConfigFieldReader[ConfigMemorySize] {
    def read(config: Config, path: String): ConfigMemorySize = {
      config.getMemorySize(path)
    }
    def readIterable(config: Config, path: String): Iterable[ConfigMemorySize] = {
      config.getMemorySizeList(path).asScala
    }
  }

  implicit object DurationConfigFieldReader extends ConfigFieldReader[Duration] {
    def read(config: Config, path: String): Duration = {
      config.getDuration(path)
    }
    def readIterable(config: Config, path: String): Iterable[Duration] = {
      config.getDurationList(path).asScala
    }
  }

  implicit object ConfigListObjectConfigFieldReader extends ConfigFieldReader[ConfigList] {
    def read(config: Config, path: String): ConfigList = {
      config.getList(path)
    }
    def readIterable(config: Config, path: String): Iterable[ConfigList] = ???
  }

  implicit object AnyRefConfigFieldReader extends ConfigFieldReader[AnyRef] {
    def read(config: Config, path: String): AnyRef = {
      config.getAnyRef(path)
    }
    def readIterable(config: Config, path: String): Iterable[AnyRef] = {
      config.getAnyRefList(path).asScala.toList.asInstanceOf[List[AnyRef]]
    }
  }

}
