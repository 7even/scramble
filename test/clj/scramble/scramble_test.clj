(ns scramble.scramble-test
  (:require [clojure.test :refer [deftest is]]
            [scramble.scramble :as sut]))

(deftest scramble?-test
  (is (true? (sut/scramble? "rekqodlw" "world")))
  (is (true? (sut/scramble? "cedewaraaossoqqyt" "codewars")))
  (is (false? (sut/scramble? "katas" "steak"))))
