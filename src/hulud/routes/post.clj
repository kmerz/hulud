(ns hulud.routes.post
  (:use compojure.core)
  (:require [hulud.views.layout :as layout]
            [hulud.util :as util]
            [noir.session :as session]
            [hulud.models.db :as db]))

(defn show
  [& [id]]
  (layout/render "post.html"
                 {:user (session/get :user)
                  :item (db/get-post-for-html id)}))

(defn form
  [& [title content error]]
  (layout/render "new-post.html"
                 {:error error
                  :title title
                  :url "/post/new"
                  :user (session/get :user)
                  :content content}))

(defn edit-form
  [& [id]]
  (let [post (db/get-post-by-id id)]
    (form post)))

(defn save
  [& [title content id]]
  (cond
    (empty? content)
    (form title content "No content given")
    (empty? title)
    (form title content "No title given")
    (empty? id)
    (do
      (let [id (last (last (db/save-post title content)))]
        (show id)))
    :else
    (do
      (db/update-post title content id)
      (show id))))

(defroutes post-routes
  (GET "/post/new" [] (form))
  (POST "/post/new" [title content] (save title content))
  (GET "/post/:id" [id] (show id))
  (GET "/post/:id/edit" [id] (edit-form id))
  (POST "/post/:id" [title content id] (save title content id)))
