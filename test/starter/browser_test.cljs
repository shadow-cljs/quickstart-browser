(ns starter.browser-test
  (:require [clojure.test :refer [deftest is testing]]
            [starter.browser :as browser]))

(deftest exported-function
  (testing "start"
    (is (fn? browser/start))
    (is (-> #'browser/start
            meta
            :dev/after-load)))

  (testing "stop"
    (is (fn? browser/stop))
    (is (-> #'browser/stop
            meta
            :dev/before-load)))

  (testing "init"
    (is (fn? browser/init))))
