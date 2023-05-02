(ns scramble.core
  (:require [bidi.ring :refer [make-handler]]
            [clojure.spec.alpha :as s]
            [muuntaja.middleware :as m]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [bad-request content-type not-found response]]
            [scramble.html :refer [page]]
            [scramble.scramble :refer [scramble?]]))

(s/def :scramble/str1 string?)
(s/def :scramble/str2 string?)

(s/def ::scramble
  (s/keys :req [:scramble/str1 :scramble/str2]))

(defn- scramble-handler [{params :body-params}]
  (if (s/valid? ::scramble params)
    (response {:scramble/success? (scramble? (:scramble/str1 params)
                                             (:scramble/str2 params))})
    (bad-request {:scramble/error "Invalid params"})))

(def routes
  ["" {:get {"/" (constantly (-> (response page)
                                 (content-type "text/html")))}
       :post {"/api/scramble" scramble-handler}
       true (constantly (not-found "Not found"))}])

(def handler
  (-> (make-handler routes)
      m/wrap-format
      (wrap-resource "public")))

(defonce server
  (atom nil))

(defn start []
  (reset! server
          (run-jetty #'handler {:port 7000
                                :join? false})))

(defn stop []
  (.stop @server)
  (reset! server nil))
