(ns scramble.ui.core
  "A simple Reagent app with a form, 2 inputs and a submit button. Includes
  clientside validation, sending requests to the server and handling errors."
  (:require [ajax.core :refer [POST]]
            [ajax.edn :refer [edn-request-format edn-response-format]]
            [clojure.string :as str]
            [reagent.core :as r]
            [reagent.dom :as rd]))

(defonce
  ^{:doc "An atom holding values of form inputs."}
  field-values
  (r/atom {:scramble/str1 ""
           :scramble/str2 ""}))

(defonce
  ^{:doc "An atom holding the result of checking the form values.
  May have `:scramble/success?` key if the values were checked
  by the backend app or `:validation/errors` key if the clientside
  validation found errors."}
  result
  (r/atom {}))

(defn- get-errors
  "Given a map with two strings, checks both - if any of them is empty or
  includes characters other than lowercase latin letters, returns errors."
  [{:scramble/keys [str1 str2]}]
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

(defn- submit
  "Validates values from `field-values`, then if they're valid, makes a request
  to the server and puts the response inside `result`; otherwise puts
  the validation errors into `result`."
  []
  (let [values @field-values
        errors (get-errors values)]
    (if (seq errors)
      (reset! result errors)
      (POST "/api/scramble"
            {:params values
             :format (edn-request-format)
             :response-format (edn-response-format)
             :handler (fn [response]
                        (reset! result response))
             :error-handler (fn [response]
                              (println "Something went wrong:" (pr-str response)))}))))

(defn- str-input
  "Renders an input for a string and a list of related errors below.
  `state-key` is used to lookup the current input value inside `field-values`
  while `placeholder` serves as a placeholder for an input."
  [state-key placeholder]
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

(defn interface
  "Renders the page."
  []
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
     [:button "Scramble"]
     (when-some [success? (:scramble/success? @result)]
       (if success?
         [:div {:style {:color :green}}
          "Match"]
         [:div {:style {:color :red}}
          "Mismatch"]))]]])

(defn render
  "Main entry point of the application; mounts the Reagent app on
  the root element of the page."
  []
  (rd/render [interface]
             (js/document.getElementById "root")))
