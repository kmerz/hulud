(ns hulud.models.post
  (:use korma.core
        [korma.db :only (defdb)])
  (:use markdown.core)
  (:require [hulud.models.schema :as schema]))

(defdb db schema/db-spec)

(defn clob-to-string [clob]
  (if (not (nil? clob))
    (with-open [rdr (java.io.BufferedReader. (.getCharacterStream clob))]
      (clojure.string/join "\n" (line-seq rdr)))))

(defentity posts
   (pk :id)
   (entity-fields :id :timestamp :title :content :public)
   (database db)
   (transform #(assoc % :content (clob-to-string (:content %)))))

(defn save-post
  [title content]
  (insert posts
          (values {:title title
                   :content content
                   :timestamp (new java.util.Date)})))

(defn delete-post
  [id]
  (delete posts
    (where {:id id})))

(defn count-posts
  []
  (:cnt (first (select posts (aggregate (count :*) :cnt)))))

(defn calc-page-count
  []
  (int (Math/ceil (/ (count-posts) posts-per-page))))


(defn update-post
  [title content id]
  (update posts
    (set-fields
      {:title title
       :content content
       :timestamp (new java.util.Date)})
      (where {:id id})))

(def posts-per-page 4)

(defn offset-from-page-num
  [page-num]
  (* (- page-num 1) posts-per-page))

(defn get-posts
  [page]
  (select posts (order :id :DESC) (limit posts-per-page)
          (offset (offset-from-page-num page))))

(defn get-posts-for-html
  [page]
  (map #(update-in % [:content] md-to-html-string) (get-posts page)))

(defn get-post-by-id
  [id]
  (first (select posts (where {:id id}))))

(defn get-post-for-html
  [id]
  (update-in (get-post-by-id id) [:content] md-to-html-string))

