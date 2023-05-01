(ns scramble.core
  (:require [bidi.ring :refer [make-handler]]
            [clojure.spec.alpha :as s]
            [muuntaja.middleware :as m]
            [ring.adapter.jetty :refer [run-jetty]]
            [scramble.scramble :refer [scramble?]]))

(s/def :scramble/str1 string?)
(s/def :scramble/str2 string?)

(s/def ::scramble
  (s/keys :req [:scramble/str1 :scramble/str2]))

(defn- scramble-handler [{params :body-params}]
  (if (s/valid? ::scramble params)
    {:status 200
     :body {:scramble/success? (scramble? (:scramble/str1 params)
                                          (:scramble/str2 params))}}
    {:status 422
     :body {:scramble/error "Invalid params"}}))

(def routes
  ["" {:post {"/api/scramble" scramble-handler}
       true (fn [req]
              {:status 404
               :body "Not found"})}])

(def handler
  (-> (make-handler routes)
      m/wrap-format))

(defonce server
  (atom nil))

(defn start []
  (reset! server
          (run-jetty handler {:port 7000
                              :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))
