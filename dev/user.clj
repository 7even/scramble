(ns user
  (:require [scramble.core :refer [start stop]]
            [shadow.cljs.devtools.api :as shadow]
            [shadow.cljs.devtools.server :as server]))

(defn cljs-repl []
  (server/start!)
  (shadow/watch :main)
  (shadow/nrepl-select :main))
