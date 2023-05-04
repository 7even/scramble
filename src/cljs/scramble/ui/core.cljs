(ns scramble.ui.core
  (:require [ajax.core :refer [POST]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [reagent.core :as r]
            [reagent.dom :as rd]))

(defonce field-values
  (r/atom {:scramble/str1 ""
           :scramble/str2 ""}))

(defonce result
  (r/atom nil))

(defn- submit []
  (POST "/api/scramble"
        {:params @field-values
         :format (edn-request-format)
         :response-format (edn-response-format)
         :handler (fn [{:scramble/keys [success?]}]
                    (reset! result success?))}))

(defn- str-input [state-key placeholder]
  [:input {:type :text
           :placeholder placeholder
           :value (get @field-values state-key)
           :on-change #(swap! field-values assoc state-key (-> % .-target .-value))}])

(defn interface []
  [:div {:style {:display :flex
                 :justify-content :center
                 :align-items :center
                 :width "100wh"
                 :height "100vh"
                 :font-size "14pt"}}
   [:form {:style {:width "300px"
                   :display :flex
                   :flex-direction :column
                   :gap "1rem"}
           :on-submit (fn [e]
                        (.preventDefault e)
                        (submit))}
    [:div "This tool allows you to enter 2 words and check if a portion of characters from the first word can be rearranged to build the second word."]
    [str-input :scramble/str1 "rekqodlw"]
    [str-input :scramble/str2 "world"]
    [:div {:style {:display :flex
                   :gap "1rem"}}
     [:button {:on-click (fn [])}
      "Scramble"]
     (when-some [success? @result]
       (if success?
         [:div {:style {:color :green}}
          "Match"]
         [:div {:style {:color :red}}
          "Mismatch"]))]]])

(defn render []
  (rd/render [interface]
             (js/document.getElementById "root")))
