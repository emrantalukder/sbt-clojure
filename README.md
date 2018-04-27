# sbt-clojure

* written for sbt 1.1.0
* rewrite of [Geal/sbt-clojure](https://github.com/Geal/sbt-clojure)

## Usage

* add plugin to `project/plugins.sbt`
```
lazy val clojurePlugin = RootProject(uri("https://github.com/terminally-chill/sbt-clojure.git"))
lazy val root = (project in file(".")).dependsOn(clojurePlugin)
```

* enable plugin in `build.sbt`
```
lazy val root = (project in file("."))
  .enablePlugins(ClojurePlugin)
```

* create clojure source directory at `src/main/clojure`

## Interop Example:

* `interop.clj` to be invoked from scala source

```
(ns com.terminallychill.clj.interop
  (:gen-class :methods [#^{:static true} [info [] void]
                        ^:static [get [String] String]
                        ]))
;; static methods
(defn -info []
  (println "[x] clojure version: " (clojure.core/clojure-version)))

(def foo-map {:a "a" :b "b" :c "c"})

(defn -get [key]
  (foo-map (keyword key)))
```

* `Main.scala` will call `get` and `info` functions

```
import com.terminallychill.clj._

object ClojureInterop extends App {
  // static clojure method:
  interop.info()

  // reads from clj data structure:
  val v = interop.get("a")
  println(v)
}
```
