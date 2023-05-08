(ns build
  (:require [clojure.tools.build.api :as b]))

(def class-dir
  "target/classes")

(def basis
  (b/create-basis {:project "deps.edn"
                   :aliases [:clj]}))

(defn uberjar [_]
  (b/delete {:path "target"})
  (b/copy-dir {:src-dirs ["src/clj" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src/clj"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file "target/scramble.jar"
           :basis basis
           :main 'scramble.core}))
