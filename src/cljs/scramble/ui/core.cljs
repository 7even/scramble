(ns scramble.ui.core
  (:require [ajax.core :refer [POST]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [clojure.string :as str]
            [reagent.core :as r]
            [reagent.dom :as rd]))

(defonce field-values
  (r/atom {:scramble/str1 ""
           :scramble/str2 ""}))

(defonce result
  (r/atom {}))

(defn- get-errors [{:scramble/keys [str1 str2]}]
  (let [add-error (fnil conj [])]
    (cond-> {}
      (str/blank? str1)
      (update-in [:validation/errors :scramble/str1]
                 add-error
                 "First word is required")

      (not (re-matches #"[a-z]*" str1))
      (update-in [:validation/errors :scramble/str1]
                 add-error
                 "First word must consist of lowercase letters")

      (str/blank? str2)
      (update-in [:validation/errors :scramble/str2]
                 add-error
                 "Second word is required")

      (not (re-matches #"[a-z]*" str2))
      (update-in [:validation/errors :scramble/str2]
                 add-error
                 "Second word must consist of lowercase letters"))))

(defn- submit []
  (let [values @field-values
        errors (get-errors values)]
    (if (seq errors)
      (reset! result errors)
      (POST "/api/scramble"
            {:params values
             :format (edn-request-format)
             :response-format (edn-response-format)
             ;; TODO: handle errors
             :handler (fn [response]
                        (reset! result response))}))))

(defn- str-input [state-key placeholder]
  [:div {:style {:display :flex
                 :flex-direction :column
                 :gap "0.3rem"}}
   [:input {:type :text
            :placeholder placeholder
            :value (get @field-values state-key)
            :on-change #(swap! field-values assoc state-key (-> % .-target .-value))}]
   (let [errors (get-in @result [:validation/errors state-key])]
     (when (seq errors)
       [:div {:style {:color :red}}
        (doall
         (for [error errors]
           ^{:key error}
           [:div {:style {:font-size "12pt"}}
            error]))]))])

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
                   :justify-content :space-between}}
     [:button {:on-click (fn [])}
      "Scramble"]
     (when-some [success? (:scramble/success? @result)]
       (if success?
         [:div {:style {:color :green}}
          "Match"]
         [:div {:style {:color :red}}
          "Mismatch"]))]]])

(defn render []
  (rd/render [interface]
             (js/document.getElementById "root")))
