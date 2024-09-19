package org.mulesoft.common.io

import java.io.{FileInputStream, FileOutputStream, InputStreamReader, OutputStreamWriter, File => JFile}

import scala.concurrent.ExecutionContext

/**
  * Implementation of a SyncFile for the JVM
  */
protected class JvmSyncFile(val fileSystem: FileSystem, val path: String) extends SyncFile with JvmBaseFile {

  private val file = new JFile(path)

  override def async: AsyncFile = new JvmAsyncFile(this)

  override def parent: String      = file.getParent
  override def name: String        = file.getName
  override def list: Array[String] = file.list()
  override def delete: Unit        = file.delete()

  override def mkdir(implicit ctx: ExecutionContext = global): Unit = file.mkdirs()

  override def read(encoding: String)(implicit ctx: ExecutionContext = global): CharSequence = {
    val fis  = new InputStreamReader(new FileInputStream(file), encoding)
    val data = new Array[Char](file.length.toInt)
    val n    = fis.read(data)
    fis.close()
    if (n == data.length) data.mkString else data.mkString.subSequence(0, n)
  }

  override def write(data: CharSequence, encoding: String)(implicit ctx: ExecutionContext = global): Unit = {
    val fos = new OutputStreamWriter(new FileOutputStream(file), encoding)
    fos.write(data.toString)
    fos.close()
  }

  override def exists(implicit ctx: ExecutionContext = global): Boolean      = file.exists()
  override def isDirectory(implicit ctx: ExecutionContext = global): Boolean = file.isDirectory
  override def isFile(implicit ctx: ExecutionContext = global): Boolean      = file.isFile
}
