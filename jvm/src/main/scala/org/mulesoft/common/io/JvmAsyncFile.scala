package org.mulesoft.common.io

import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of a AsyncFile for the JVM
  * * @todo better handling of errors, Real async mode
  */
protected class JvmAsyncFile(private val syncFile: JvmSyncFile) extends AsyncFile with JvmBaseFile {

  val path: String           = syncFile.path
  val fileSystem: FileSystem = syncFile.fileSystem

  override def sync: SyncFile = syncFile
  override def parent: String = syncFile.parent
  override def name: String   = syncFile.name

  override def delete: Future[Unit]                                         = Future.successful(syncFile.delete)
  override def list: Future[Array[String]]                                  = Future.successful(syncFile.list)
  override def mkdir(implicit ctx: ExecutionContext = global): Future[Unit] = Future(syncFile.mkdir)
  override def read(encoding: String)(implicit ctx: ExecutionContext = global): Future[CharSequence] =
    Future(syncFile.read(encoding))
  override def write(data: CharSequence, encoding: String)(implicit ctx: ExecutionContext = global): Future[Unit] =
    Future(syncFile.write(data, encoding))
  override def exists(implicit ctx: ExecutionContext = global): Future[Boolean]      = Future(syncFile.exists)
  override def isDirectory(implicit ctx: ExecutionContext = global): Future[Boolean] = Future(syncFile.isDirectory)
  override def isFile(implicit ctx: ExecutionContext = global): Future[Boolean]      = Future(syncFile.isFile)
}
