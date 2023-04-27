(ns scramble.scramble)

(defn scramble?
  "Returns true if `str2` can be assembled from a portion of characters
  from `str1`, false otherwise."
  [str1 str2]
  (->> str2
       (reduce (fn [acc letter]
                 (if (pos? (get acc letter 0))
                   (update acc letter dec)
                   (reduced nil)))
               (frequencies str1))
       some?))
