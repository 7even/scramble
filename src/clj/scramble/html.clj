(ns scramble.html
  (:require [hiccup.page :refer [html5]]))

(def page
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:title "Scramble"]]
   [:body
    [:div#root]
    [:script {:src "/js/main.js"}]]))
