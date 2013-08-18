(ns hulud.routes.home
  (:use compojure.core)
  (:require [hulud.views.layout :as layout]
            [hulud.util :as util]
            [hulud.models.db :db]))

(defn home-page
  [& [title content error]]
  (layout/render "home.html"
                 {:error error
                  :title title
                  :content content
                  :posts (db/get-posts)}))

(defn save-post
  [title content]
  (cond
    (empty? title)
    (home-page title content "No title given")
    (empty? content)
    (home-page title content "No content given")
    :else
    (do
      (db/save-message title content)
      (home-page))))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/" [title content] (save-message title content))
  (GET "/about" [] (about-page)))
