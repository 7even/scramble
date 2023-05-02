(ns scramble.core-test
  (:require [clojure.edn :as edn]
            [clojure.test :refer [deftest testing is]]
            [ring.mock.request :as mock]
            [scramble.core :as sut]))

(deftest handler-test
  (letfn [(get-response [params]
            (let [request (-> (mock/request :post "/api/scramble")
                              (mock/header "Accept" "application/edn")
                              (mock/content-type "application/edn")
                              (mock/body (pr-str params)))
                  response (sut/handler request)]
              (update response
                      :body
                      (comp edn/read-string slurp))))]
    (testing "with a valid scramble"
      (let [params {:scramble/str1 "foobar"
                    :scramble/str2 "foo"}
            response (get-response params)]
        (is (= 200 (:status response)))
        (is (= {:scramble/success? true}
               (:body response)))))
    (testing "with an invalid scramble"
      (let [params {:scramble/str1 "foobar"
                    :scramble/str2 "foobaz"}
            response (get-response params)]
        (is (= 200 (:status response)))
        (is (= {:scramble/success? false}
               (:body response)))))
    (testing "with invalid params"
      (let [params {:scramble/str1 :foo
                    :scramble/str3 4}
            response (get-response params)]
        (is (= 400 (:status response)))
        (is (= {:scramble/error "Invalid params"}
               (:body response)))))))
