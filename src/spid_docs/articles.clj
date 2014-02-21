(ns spid-docs.articles
  (:require [clojure.string :as str]
            [spid-docs.formatting :refer [to-html]]))

(defn- to-page-url [[path _]]
  (str/replace path #"\.md$" "/"))

(defn- create-post [[_ markdown]]
  {:title ""
   :body (to-html markdown)})

(defn create-pages [articles]
  (->> articles
       (map (juxt to-page-url #(partial create-post %)))
       (into {})))