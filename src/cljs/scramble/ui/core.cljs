(ns scramble.ui.core
  (:require [reagent.dom :as rd]))

(defn interface []
  [:div {:style {:display :flex
                 :justify-content :center
                 :align-items :center
                 :width "100wh"
                 :height "100vh"
                 :font-size "14pt"}}
   "Scramble"])

(defn render []
  (rd/render [interface]
             (js/document.getElementById "root")))
