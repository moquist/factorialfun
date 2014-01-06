(ns factorialfun.core-test
  (:require [clojure.test :refer :all]
            [factorialfun.core :refer :all]))

(deftest sanity?
  (testing "fns"
    (testing "f0"
      (is (= 24 (f0 4))))
    (testing "f1"
      (is (= 24 (f1 4))))
    (testing "f2"
      (is (= 24 (f2 4)))))
  (testing "seqs"
    (testing "f3-seq-nth"
      (is (= 24 (f3-seq-n 4))))
    (testing "f3-seq-take"
      (is (= '(1 2 6 24 120 720 5040) (f3-seq-take 7))))))

