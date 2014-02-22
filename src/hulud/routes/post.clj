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
  [& [{title :title content :content error :error}]]
  (if (session/get :user)
    (layout/render "new-post.html"
      {:error error
       :title title
       :url "/post/new"
       :user (session/get :user)
       :content content})))

(defn edit-form
  [& [id]]
  (if (session/get :user)
    (let [post (db/get-post-by-id id)]
      (form post))))

(defn delete
  [& [id]]
    (if (session/get :user)
      (db/delete-post id))
    (form))

(defn save
  [& [title content id]]
  (cond
    (not (session/get :user))
    (form {:error "No user logged in"})
    (empty? content)
    (form {:title title :error "No content given"})
    (empty? title)
    (form {:content content :error "No title given"})
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
  (POST "/post/:id" [title content id] (save title content id))
  (GET "/post/:id/delete" [id] (delete id)))
