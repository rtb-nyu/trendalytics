package com.trendalytics

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import org.apache.hadoop.conf._
import org.apache.hadoop.fs._

class HDFSManager {
  private val conf = new Configuration()
  // private val hdfsCoreSitePath = new Path("core-site.xml")
  // private val hdfsHDFSSitePath = new Path("hdfs-site.xml")

  // conf.addResource(hdfsCoreSitePath)
  // conf.addResource(hdfsHDFSSitePath)

  private val fileSystem = FileSystem.get(conf)

  def saveFile(filepath: String): Unit = {
    val file = new File(filepath)
<<<<<<< HEAD
    val out = fileSystem.create(new Path(filepath))
=======
    val out = fileSystem.create(new Path(file.getName))
>>>>>>> e72e46a... added hdfs commands
    val in = new BufferedInputStream(new FileInputStream(file))
    var b = new Array[Byte](1024)
    var numBytes = in.read(b)
    while (numBytes > 0) {
      out.write(b, 0, numBytes)
      numBytes = in.read(b)
    }
    in.close()
    out.close()
  }

  def removeFile(filename: String): Boolean = {
    val path = new Path(filename)
    fileSystem.delete(path, true)
  }

  def getFile(filename: String): InputStream = {
    val path = new Path(filename)
    fileSystem.open(path)
  }

  def createFolder(folderPath: String): Unit = {
    val path = new Path(folderPath)
    if (!fileSystem.exists(path)) {
      fileSystem.mkdirs(path)
    }
  }
<<<<<<< HEAD
<<<<<<< HEAD

  def deleteFolder(folderPath: String): Unit = {
    val path = new Path(folderPath)
    if (fileSystem.exists(path)) {
      fileSystem.delete(path, true)
    }
  }

  def isFilePresent(filename: String): Boolean = {
    fileSystem.exists(new Path(filename))
  }
=======
>>>>>>> e72e46a... added hdfs commands
=======

  def isFilePresent(filename: String): Boolean = {
    fileSystem.exists(new Path(filename))
  }
>>>>>>> c82d2ed... adding out.csv file
}