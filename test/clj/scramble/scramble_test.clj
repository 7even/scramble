(ns scramble.scramble-test
  (:require [clojure.test :refer [deftest is]]
            [scramble.scramble :as sut]))

(deftest scramble?-test
  (is (sut/scramble? "rekqodlw" "world"))
  (is (sut/scramble? "cedewaraaossoqqyt" "codewars"))
  #_(is (not (sut/scramble? "katas" "steak"))))
