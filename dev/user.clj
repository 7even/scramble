(ns user
  (:require [scramble.core :refer [start stop]]
            [shadow.cljs.devtools.api :as shadow]
            [shadow.cljs.devtools.server :as server]))

(defonce
  ^{:doc "An atom holding the currently running server (if any)."}
  server
  (atom nil))

(defn go
  "Starts the server and puts it inside `server` atom."
  []
  (reset! server (start)))

(defn halt
  "Stops the server inside `server` and resets the atom to nil."
  []
  (swap! server stop))

(defonce
  ^{:doc "An atom indicating if the server was ever launched."}
  app-started?
  (atom false))

;; start the server when REPL is launched and this namespace is loaded
;; for the first time (but not when this namespace is reloaded)
(when-not @app-started?
  (go)
  (reset! app-started? true))

(defn cljs-repl
  "Starts the shadow-cljs watcher and a ClojureScript REPL.
  Supposed to be used as cider-custom-cljs-repl-init-form
  (see .dir-locals.el)."
  []
  (server/start!)
  (shadow/watch :main)
  (shadow/nrepl-select :main))
