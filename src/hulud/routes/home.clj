(ns hulud.routes.home
  (:use compojure.core)
  (:require [hulud.views.layout :as layout]
            [hulud.util :as util]
            [hulud.models.db :as db]))

(defn home-page
  [& [title content error]]
  (layout/render "home.html"
                 {:error error
                  :title title
                  :content content
                  :posts (db/get-posts)}))
(defn new-post
  [& [title content error]]
  (layout/render "new-post.html"
                 {:error error
                  :title title
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
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/new-post" [] (new-post))
  (POST "/new-post" [title content] (save-post title content))
  (GET "/about" [] (about-page)))
