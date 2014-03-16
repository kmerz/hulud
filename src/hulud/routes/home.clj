(ns hulud.routes.home
  (:use compojure.core)
  (:require [hulud.views.layout :as layout]
            [hulud.util :as util]
            [noir.session :as session]
            [hulud.models.post :as post-db]
            [hulud.models.user :as user-db]))

(defn calc-page-count [post-count]
  (+ (/ (- post-count (mod post-count 4)) 4) 1))

(defn home-page
  [& [{error :error page :page}]]
  (layout/render "home.html"
                 {:page page
                  :prev (- page 1)
                  :next (+ page 1)
                  :pagelast (calc-page-count (post-db/count-posts))
                  :page-range (range 1
                    (+ (calc-page-count (post-db/count-posts)) 1))
                  :user (session/get :user)
                  :posts (post-db/get-posts-for-html page)}))

(defn login-page
  [& [user-name error]]
  (layout/render "login.html"
                 {:error error
                  :user (session/get :user)
                  :user-name user-name}))

(defn login-user
  [user]
  (session/put! :user (:id user))
  (home-page :page 1))

(defn logout-user
  [& []]
  (session/clear!)
  (home-page :page 1))

(defn auth-user
  [name password]
  (if (user-db/correct-password-for-user? name password)
    (login-user (user-db/get-user-by-name name))
    (login-page name "Wrong password")))

(defn about-page []
  (layout/render "about.html"
                 {:user (session/get :user)}))

(defn page-to-int
  [page-num]
      (if (re-matches (re-pattern "\\d+") page-num)
        (read-string page-num)
        1))

(defroutes home-routes
  (GET "/" [] (home-page {:page 1}))
  (GET "/about" [] (about-page))
  (GET "/login" [] (login-page))
  (POST "/login" [user-name password] (auth-user user-name password))
  (GET "/logout" [] logout-user)
  (GET "/page/:id" [id] (home-page {:page (page-to-int id)})))
