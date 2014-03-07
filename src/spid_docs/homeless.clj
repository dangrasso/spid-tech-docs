(ns spid-docs.homeless
  "Where functions that have nowhere to live huddle together around a fire.")

(defn wrap-utf-8
  "This function works around the fact that Ring simply chooses the default JVM
  encoding for the response encoding. This is not desirable, we always want to
  send UTF-8."
  [handler]
  (fn [request]
    (when-let [response (handler request)]
      (if (.contains (get-in response [:headers "Content-Type"]) ";")
        response
        (if (string? (:body response))
          (update-in response [:headers "Content-Type"] #(str % "; charset=utf-8"))
          response)))))

(defn update-vals
  "Returns a new map with `f` applied to all values in `m`."
  [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn min*
  "Like min, but takes a list - and 0 elements is okay."
  [vals]
  (when (seq vals) (apply min vals)))

(defn subs*
  "Like subs, but safe - ie, doesn't barf on too short."
  [s len]
  (if (> (count s) len)
    (subs s len)
    s))

(defn find-common-indent-column
  "Find the lowest number of spaces that all lines have as a common
   prefix. Except, don't count empty lines."
  [lines]
  (->> lines
       (remove #(empty? %))
       (map #(count (re-find #"^ +" %)))
       (min*)))

(defn unindent
  "Given a block of code, if all lines are indented, this removes the
   preceeding whitespace that is common to all lines."
  [lines]
  (let [superflous-spaces (find-common-indent-column lines)]
    (map #(subs* % superflous-spaces) lines)))

(defn update-existing
  "Given a map m and pairs of 'selector' and function, update the map only if
   the selector describes existing entries. Selectors are vectors like the ones
   accepted by Clojure's update-in and get-in, e.g. [:some :key] to update 42
   in {:some {:key 42}}. The function will be called with the existing value,
   and its return value will replace it in the new map that is returned.

   An example is due:

   (update-existing {:name {:first \"Christian\" :last \"Johansen\"}}
                    [:name :first] (fn [firstname] (capitalize firstname))
                    [:name :middle] (fn [middlename] (.toLowerCase middle)))
   ;;=> {:name {:first \"CHRISTIAN\" :last \"Johansen\"}}

   As you can see, there's no middle name because none existed."
  [m & forms]
  (if (-> forms count (mod 2) (= 0) not)
    (throw (Exception. "update-if needs an even number of forms")))
  (->> forms
       (partition 2)
       (reduce (fn [memo [path val]]
                 (if-let [curr (get-in memo path)]
                   (assoc-in memo path (if (fn? val) (val curr) val))
                   memo)) m)))