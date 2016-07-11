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
import java.nio.file.{ Path, Paths }

import scala.reflect.ClassTag
import scala.collection.JavaConverters._

import com.typesafe.config._

object ConfigUtils {

  /** Allows to treat a Config object like a Map[A] */
  implicit class ConfigWrapper(val config: Config) extends AnyRef {

    /** Retrieves the value of type A from the Config object.
      *
      * Throws ConfigException.Missing if the value is missing.
      */
    def apply[A: ConfigFieldReader](path: String): A = {
      implicitly[ConfigFieldReader[A]].read(config, path)
    }

    /** Retrieves the value of type A from the Config object.
      *
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
  abstract class ConfigFieldReader[A: ClassTag] {
    def read(config: Config, path: String): A
  }

  implicit object BooleanConfigFieldReader extends ConfigFieldReader[Boolean] {
    def read(config: Config, path: String): Boolean = {
      config.getBoolean(path)
    }
  }

  implicit object IntConfigFieldReader extends ConfigFieldReader[Int] {
    def read(config: Config, path: String): Int = {
      config.getInt(path)
    }
  }

  implicit object LongConfigFieldReader extends ConfigFieldReader[Long] {
    def read(config: Config, path: String): Long = {
      config.getLong(path)
    }
  }

  implicit object DoubleConfigFieldReader extends ConfigFieldReader[Double] {
    def read(config: Config, path: String): Double = {
      config.getDouble(path)
    }
  }

  implicit object StringConfigFieldReader extends ConfigFieldReader[String] {
    def read(config: Config, path: String): String = {
      config.getString(path)
    }
  }

  implicit object URIConfigFieldReader extends ConfigFieldReader[URI] {
    def read(config: Config, path: String): URI = {
      new URI(config.getString(path))
    }
  }

  implicit object URLConfigFieldReader extends ConfigFieldReader[URL] {
    def read(config: Config, path: String): URL = {
      new URL(config.getString(path))
    }
  }

  implicit object PathConfigFieldReader extends ConfigFieldReader[Path] {
    def read(config: Config, path: String): Path = {
      Paths.get(config.getString(path))
    }
  }

  implicit object FileConfigFieldReader extends ConfigFieldReader[File] {
    def read(config: Config, path: String): File = {
      new File(config.getString(path))
    }
  }

  implicit object ConfigObjectConfigFieldReader extends ConfigFieldReader[ConfigObject] {
    def read(config: Config, path: String): ConfigObject = {
      config.getObject(path)
    }
  }

  implicit object ConfigConfigFieldReader extends ConfigFieldReader[Config] {
    def read(config: Config, path: String): Config = {
      config.getConfig(path)
    }
  }

  implicit object ConfigValueConfigFieldReader extends ConfigFieldReader[ConfigValue] {
    def read(config: Config, path: String): ConfigValue = {
      config.getValue(path)
    }
  }

  implicit object ConfigMemorySizeConfigFieldReader extends ConfigFieldReader[ConfigMemorySize] {
    def read(config: Config, path: String): ConfigMemorySize = {
      config.getMemorySize(path)
    }
  }

  implicit object DurationConfigFieldReader extends ConfigFieldReader[Duration] {
    def read(config: Config, path: String): Duration = {
      config.getDuration(path)
    }
  }

  implicit object ConfigListObjectConfigFieldReader extends ConfigFieldReader[ConfigList] {
    def read(config: Config, path: String): ConfigList = {
      config.getList(path)
    }
  }

  implicit object AnyRefConfigFieldReader extends ConfigFieldReader[AnyRef] {
    def read(config: Config, path: String): AnyRef = {
      config.getAnyRef(path)
    }
  }

  implicit object BooleanListConfigFieldReader extends ConfigFieldReader[List[Boolean]] {
    def read(config: Config, path: String): List[Boolean] = {
      config.getBooleanList(path).asScala.map(Boolean2boolean).toList
    }
  }

  implicit object IntListConfigFieldReader extends ConfigFieldReader[List[Int]] {
    def read(config: Config, path: String): List[Int] = {
      config.getIntList(path).asScala.map(Integer2int).toList
    }
  }

  implicit object LongListConfigFieldReader extends ConfigFieldReader[List[Long]] {
    def read(config: Config, path: String): List[Long] = {
      config.getLongList(path).asScala.map(Long2long).toList
    }
  }

  implicit object DoubleListConfigFieldReader extends ConfigFieldReader[List[Double]] {
    def read(config: Config, path: String): List[Double] = {
      config.getDoubleList(path).asScala.map(Double2double).toList
    }
  }

  implicit object StringListConfigFieldReader extends ConfigFieldReader[List[String]] {
    def read(config: Config, path: String): List[String] = {
      config.getStringList(path).asScala.toList
    }
  }

  implicit object URIListConfigFieldReader extends ConfigFieldReader[List[URI]] {
    def read(config: Config, path: String): List[URI] = {
      config.getStringList(path).asScala.map(new URI(_)).toList
    }
  }

  implicit object URLListConfigFieldReader extends ConfigFieldReader[List[URL]] {
    def read(config: Config, path: String): List[URL] = {
      config.getStringList(path).asScala.map(new URL(_)).toList
    }
  }

  implicit object PathListConfigFieldReader extends ConfigFieldReader[List[Path]] {
    def read(config: Config, path: String): List[Path] = {
      config.getStringList(path).asScala.map(Paths.get(_)).toList
    }
  }

  implicit object FileListConfigFieldReader extends ConfigFieldReader[List[File]] {
    def read(config: Config, path: String): List[File] = {
      config.getStringList(path).asScala.map(new File(_)).toList
    }
  }

  implicit object ConfigObjectListConfigFieldReader extends ConfigFieldReader[List[ConfigObject]] {
    def read(config: Config, path: String): List[ConfigObject] = {
      config.getObjectList(path).asScala.toList
    }
  }

  implicit object ConfigListConfigFieldReader extends ConfigFieldReader[List[Config]] {
    def read(config: Config, path: String): List[Config] = {
      config.getConfigList(path).asScala.toList
    }
  }

  implicit object ConfigMemorySizeListConfigFieldReader extends ConfigFieldReader[List[ConfigMemorySize]] {
    def read(config: Config, path: String): List[ConfigMemorySize] = {
      config.getMemorySizeList(path).asScala.toList
    }
  }

  implicit object DurationListConfigFieldReader extends ConfigFieldReader[List[Duration]] {
    def read(config: Config, path: String): List[Duration] = {
      config.getDurationList(path).asScala.toList
    }
  }

  implicit object AnyRefListConfigFieldReader extends ConfigFieldReader[List[AnyRef]] {
    def read(config: Config, path: String): List[AnyRef] = {
      config.getAnyRefList(path).asScala.toList.asInstanceOf[List[AnyRef]]
    }
  }

}
