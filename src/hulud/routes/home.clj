(ns hulud.routes.home
  (:use compojure.core)
  (:require [hulud.views.layout :as layout]
            [hulud.util :as util]
            [noir.session :as session]
            [hulud.models.db :as db]))

(defn home-page
  [& [title content error]]
  (layout/render "home.html"
                 {:error error
                  :title title
                  :user (session/get :user)
                  :content content
                  :posts (db/get-posts-for-html)}))

(defn login-page
  [& [user-name error]]
  (layout/render "login.html"
                 {:error error
                  :user (session/get :user)
                  :user-name user-name}))

(defn login-user
  [user]
  (session/put! :user (:id user))
  (home-page))

(defn logout-user
  [& []]
  (session/clear!)
  (home-page))

(defn auth-user
  [name password]
  (if (db/correct-password-for-user? name password)
    (login-user (db/get-user-by-name name))
    (login-page name "Wrong password")))

(defn about-page []
  (layout/render "about.html"
                 {:user (session/get :user)}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/login" [] (login-page))
  (POST "/login" [user-name password] (auth-user user-name password))
  (GET "/logout" [] logout-user))
