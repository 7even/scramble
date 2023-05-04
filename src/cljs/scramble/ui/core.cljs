(ns scramble.ui.core
  (:require [reagent.core :as r]
            [reagent.dom :as rd]))

(defonce field-values
  (r/atom {:str1 ""
           :str2 ""}))

(defn- submit []
  (println "values:" (pr-str @field-values)))

(defn- str-input [state-key placeholder]
  [:input {:type :text
           :placeholder placeholder
           :value (get @field-values state-key)
           :on-change #(swap! field-values assoc state-key (-> % .-target .-value))
           :style {:width "300px"}}])

(defn interface []
  [:div {:style {:display :flex
                 :justify-content :center
                 :align-items :center
                 :width "100wh"
                 :height "100vh"
                 :font-size "14pt"}}
   [:form {:style {:display :flex
                   :flex-direction :column
                   :gap "1rem"}
           :on-submit (fn [e]
                        (.preventDefault e)
                        (submit))}
    [str-input :str1 "rekqodlw"]
    [str-input :str2 "world"]
    [:div {:style {:display :flex
                   :justify-content :center}}
     [:button {:on-click (fn [])}
      "Scramble"]]]])

(defn render []
  (rd/render [interface]
             (js/document.getElementById "root")))
