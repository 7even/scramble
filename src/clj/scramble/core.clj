(ns scramble.core
  "Application's entrypoint and HTTP handling code."
  (:require [bidi.ring :refer [make-handler]]
            [clojure.spec.alpha :as s]
            [muuntaja.middleware :as m]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [bad-request content-type not-found response]]
            [scramble.html :refer [page]]
            [scramble.scramble :refer [scramble?]])
  (:gen-class))

(s/def :scramble/str1 string?)
(s/def :scramble/str2 string?)

(s/def ::scramble
  (s/keys :req [:scramble/str1 :scramble/str2]))

(defn- scramble-handler
  "Handles the requests to /api/scramble, validating the params
  using clojure.spec and calling `scramble.scramble/scramble?`
  to get the actual result."
  [{params :body-params}]
  (if (s/valid? ::scramble params)
    (response {:scramble/success? (scramble? (:scramble/str1 params)
                                             (:scramble/str2 params))})
    (bad-request {:scramble/error "Invalid params"})))

(def routes
  "Application's routes. GET / returns the HTML for the frontend page."
  ["" {:get {"/" (constantly (-> (response page)
                                 (content-type "text/html")))}
       :post {"/api/scramble" scramble-handler}
       true (constantly (not-found "Not found"))}])

(def handler
  "HTTP handler. Negotiates formats and encodes/decodes the data using
  the muuntaja library, also serves the frontend files
  from `resources/public` directory."
  (-> (make-handler routes)
      m/wrap-format
      (wrap-resource "public")))

(defn start
  "Starts the HTTP server at port 7000."
  []
  (run-jetty #'handler {:port 7000
                        :join? false}))

(defn stop
  "Stops the given `server`."
  [server]
  (.stop server))

(defn -main
  "Starts the application."
  []
  (start))
