(ns scramble.scramble)

(defn scramble?
  "Returns true if `str2` can be assembled from a portion of characters
  from `str1`, false otherwise."
  [str1 str2]
  (->> str1
       (reduce (fn [acc letter]
                 (cond
                   (empty? acc)
                   (reduced acc)

                   (not (contains? acc letter))
                   acc

                   :else
                   (let [new-count (-> acc (get letter) dec)]
                     (if (zero? new-count)
                       (dissoc acc letter)
                       (assoc acc letter new-count)))))
               (frequencies str2))
       empty?))
