import sbt._
import sbt.Keys._


object ClojurePlugin extends AutoPlugin {

  object autoImport {
    lazy val Clojure = config("clojure") extend(Compile) hide
    lazy val clojureVersion = SettingKey[String]("clojure-version", "version of clojure to use for building")
    lazy val clojureSource = SettingKey[File]("clojure-source", "clojure source directory")
    lazy val clojureCompile = TaskKey[Unit]("clojure-compile", "runs clojure compilation, occurs before normal compilation")
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    libraryDependencies += "org.clojure" % "clojure" % clojureVersion.value,
    clojureSource in Compile := (sourceDirectory in Compile).value / "clojure",
    unmanagedResourceDirectories in Compile += {(clojureSource in Compile).value},
    clojureCompile in Compile := {
      val taskStreams: TaskStreams = streams.value.asInstanceOf[TaskStreams]
      val sourceDirectory: File = (clojureSource in Compile).value
      taskStreams.log("compiling clojure source...")

      val classpath: Seq[File] = update.value.select(configurationFilter(name = "*")) ++
        Seq((classDirectory in Compile).value) ++
        Seq(sourceDirectory)

      val stubDirectory: File = (sourceManaged in compile).value
      val destinationDirectory: File = (classDirectory in Compile).value

      def clojureClazz(file: File): File = {
        val path = file.getAbsolutePath
        new File(destinationDirectory.getAbsolutePath + path.substring(sourceDirectory.getAbsolutePath().length(), path.length() - ".clj".length()) + ".class")
      }

      (sourceDirectory ** "*.clj").get map (clojureClazz) foreach { file =>
        if(file.exists())
          IO.delete(file)
      }

      ClojureCompile.compile(classpath, sourceDirectory, stubDirectory, destinationDirectory)
    },
    compile in Compile := ((compile in Compile) dependsOn(clojureCompile in Compile)).value
  )

}
