(ns scramble.core
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn handler [request]
  {:status 200
   :body "OK"})

(defonce server
  (atom nil))

(defn start []
  (reset! server
          (run-jetty handler {:port 7000
                              :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))
