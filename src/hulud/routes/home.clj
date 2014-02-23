(ns hulud.routes.home
  (:use compojure.core)
  (:require [hulud.views.layout :as layout]
            [hulud.util :as util]
            [noir.session :as session]
            [hulud.models.post :as post-db]
            [hulud.models.user :as user-db]))

(defn clac-pagination [post-count]
  (+ (/ (- post-count (mod post-count 4)) 4) 1))

(defn home-page
  [& [{error :error page :page}]]
  (layout/render "home.html"
                 {:page page
                  :page-count (clac-pagination (post-db/count-posts))
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
  (home-page 1))

(defn logout-user
  [& []]
  (session/clear!)
  (home-page 1))

(defn auth-user
  [name password]
  (if (user-db/correct-password-for-user? name password)
    (login-user (user-db/get-user-by-name name))
    (login-page name "Wrong password")))

(defn about-page []
  (layout/render "about.html"
                 {:user (session/get :user)}))

(defroutes home-routes
  (GET "/" [page] (home-page page))
  (GET "/about" [] (about-page))
  (GET "/login" [] (login-page))
  (POST "/login" [user-name password] (auth-user user-name password))
  (GET "/logout" [] logout-user))
