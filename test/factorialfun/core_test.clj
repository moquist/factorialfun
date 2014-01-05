(ns factorialfun.core-test
  (:require [clojure.test :refer :all]
            [factorialfun.core :refer :all]))

(deftest sanity?
  (testing "f0: 4"
    (is (= 24 (f0 4))))
  (testing "f1: 4"
    (is (= 24 (f1 4))))
  (testing "f2: 4"
    (is (= 24 (f2 4)))))

