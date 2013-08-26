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
                  :posts (db/get-posts)}))

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

(defn check-user
  [name password]
  (if (db/correct-password-for-user? name password)
    (login-user (db/get-user-by-name name))
    (login-page name "Wrong password")))


(defn new-post
  [& [title content error]]
  (layout/render "new-post.html"
                 {:error error
                  :title title
                  :user (session/get :user)
                  :content content}))

(defn save-post
  [title content]
  (cond
    (empty? content)
    (new-post title content "No content given")
    (empty? title)
    (new-post title content "No title given")
    :else
    (do
      (db/save-post title content)
      (home-page))))

(defn about-page []
  (layout/render "about.html"
                 {:user (session/get :user)}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/new-post" [] (new-post))
  (POST "/new-post" [title content] (save-post title content))
  (GET "/about" [] (about-page))
  (GET "/login" [] (login-page))
  (POST "/login" [user-name password] (check-user user-name password))
  (GET "/logout" [] logout-user))
