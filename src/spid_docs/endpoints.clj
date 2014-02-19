(ns spid-docs.endpoints
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [spid-docs.layout :as layout]))

(defn- render-params [heading params]
  (if (seq params)
    (list [:h3 heading]
          [:ul (map #(vector :li %) params)])))

(defn- render-http-methods [methods]
  (mapcat #(list [:h2 (:name %)]
                 (render-params "Required params" (:required %))
                 (render-params "Optional params" (:optional %))) (vals methods)))

(defn- format-name [format]
  (cond
   (= "json" format) "JSON"
   (= "jsonp" format) "JSON-P"))

(defn endpoint-url [endpoint]
  (str "/" (:path endpoint)))

(defn endpoint-path [endpoint]
  (str "/endpoints" (endpoint-url endpoint)))

(defn- render-page [endpoint context]
  (layout/page
   context
   "Endpoint"
   (list [:h1 (endpoint-url endpoint)]
         [:p (:description endpoint)]
         [:table
          [:tr [:th "Requires authentication"] [:td "?"]]
          [:tr [:th "Supported access token types"] [:td "?"]]
          [:tr [:th "Supported response format"] [:td (str/join ", " (map format-name (:valid_output_formats endpoint)))]]
          [:tr [:th "Supported filters"] [:td "?"]]
          [:tr [:th "Default filters"] [:td "?"]]
          [:tr [:th "Successful return"] [:td "?"]]]
         (render-http-methods (:httpMethods endpoint))
         [:pre (with-out-str (pprint endpoint))])))

(defn create-pages [endpoints]
  (into {} (map (juxt endpoint-path
                      #(partial render-page %)) (:data endpoints))))
