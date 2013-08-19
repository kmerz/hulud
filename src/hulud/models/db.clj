(ns hulud.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [hulud.models.schema :as schema]))

(defdb db schema/db-spec)

(defentity posts)

(defn save-post
  [title content]
  (insert posts
          (values {:title title
                   :content content
                   :timestamp (new java.util.Date)})))

(defn clob-to-string [clob]
  (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
    (apply str (line-seq rdr))))

(defn get-posts
  []
  (select posts (order :timestamp :DESC)))
