(ns hulud.models.user
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [hulud.models.schema :as schema]
            [crypto.password.bcrypt :as pw]))

(defdb db schema/db-spec)

(defentity users)

(defn get-user-by-name
  [name]
  (first (select users (where {:name name}))))

(defn save-user
  [name password email]
  (if-not (= name (:name (get-user-by-name name)))
    (insert users
      (values {:name name
               :email email
               :password (pw/encrypt password)}))))

(defn correct-password-for-user?
  [name password]
  (let [user (get-user-by-name name)]
    (if user
      (pw/check password (:password user))
      false)))

(defn change-password
  [id password]
  (update users
    (set-fields
      {:password (pw/encrypt password)})
      (where {:id id})))
