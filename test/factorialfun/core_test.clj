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
    (testing "f3-seq"
      (testing "seq-n"
        (is (= 24 (seq-n 4 f3-seq))))
      (testing "seq-take"
        (is (= '(1 2 6 24 120 720 5040) (seq-take 7 f3-seq)))))
    (testing "f4-seq"
      (testing "seq-n"
        (is (= [4 24] (seq-n 4 f4-seq))))
      (testing "seq-take"
        (is (= '([1 1] [2 2] [3 6] [4 24] [5 120] [6 720] [7 5040]) (seq-take 7 f4-seq)))))))

