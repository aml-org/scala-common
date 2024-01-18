package org.mulesoft.common.io

import org.mulesoft.common.core._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

/**
  * A File Object abstraction (Similar to java.util.File) but with implementations in Js and JVM.
  */
trait File {

  protected val global: ExecutionContext

  /** Returns an async view of the file */
  def async: AsyncFile

  /** Returns a sync view of the file */
  def sync: SyncFile

  /** Returns the Filesystem for this file */
  def fileSystem: FileSystem

  /** The parent File */
  def parentFile: File

  /** A file into this file directory */
  def /(name: String): File

  /** The whole file path */
  def path: String

  /**
    * Returns the pathname string of this abstract pathname's parent, or empty
    * if this pathname does not name a parent directory.
    */
  def parent: String

  /**
    * Returns the name of the file or directory denoted by this abstract
    * pathname.  This is just the last name in the pathname's name sequence.
    */
  def name: String

  /** Get a new file replacing the extension */
  def withExt(newExt: String): File

  override def toString: String = path
}

protected[io] trait FileProto[F[_]] extends File {

  /** Delete a File */
  def delete: F[Unit]

  /** list the contents of a directory. */
  def list: F[Array[String]]

  /** Create a directory. */
  def mkdir(implicit ctx: ExecutionContext = global): F[Unit]

  /** Read the file. */
  def read(encoding: String = Utf8)(implicit ctx: ExecutionContext = global): F[CharSequence]

  /** Write to the file. */
  def write(data: CharSequence, encoding: String = Utf8)(implicit ctx: ExecutionContext = global): F[Unit]

  /** Returns true if the File exists */
  def exists(implicit ctx: ExecutionContext = global): F[Boolean]

  /** Returns true if the File is a directory */
  def isDirectory(implicit ctx: ExecutionContext = global): F[Boolean]

  /** Returns true if the File is a normal File */
  def isFile(implicit ctx: ExecutionContext = global): F[Boolean]

}

trait AsyncFile extends FileProto[Future] {
  override def async: AsyncFile                   = this
  override def /(name: String): AsyncFile         = fileSystem.asyncFile(this, name)
  override def parentFile: AsyncFile              = fileSystem.asyncFile(this.parent)
  override def withExt(newExt: String): AsyncFile = fileSystem.asyncFile(path replaceExtension newExt)
}

trait SyncFile extends FileProto[Id] {
  override def sync: SyncFile                    = this
  override def /(name: String): SyncFile         = fileSystem.syncFile(this, name)
  override def parentFile: SyncFile              = fileSystem.syncFile(this.parent)
  override def withExt(newExt: String): SyncFile = fileSystem.syncFile(path replaceExtension newExt)
}
