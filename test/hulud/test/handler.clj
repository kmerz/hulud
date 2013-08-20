(ns hulud.test.handler
  (:use clojure.test
        ring.mock.request
        hulud.handler))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (not (= (:body response) "")))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))
