;; https://gist.githubusercontent.com/pepijndevos/647003/raw/ded781baa15a372b869397d4cb31ab13692dd00a/convert.clj

(ns mad-sounds.convert
  (:require [clojure.zip :as z])
  (:use [overtone.helpers.lib :only [overtone-ugen-name]]))


(defmacro -?> [& forms]
  `(try (-> ~@forms)
      (catch NullPointerException _# nil)))


(defn prepend-child
  [loc item]
  (z/replace loc (z/make-node loc (z/node loc) (concat [item] (z/children loc)))))

(defn zip-map [f zipper]
  (first (drop-while (complement z/end?) (iterate (comp z/next f) zipper))))

(defn tokenize [code]
  (z/root (reduce #(cond
                  (#{\space \,} %2) (-> %1
                                      (z/insert-right "")
                                      z/right)
                  (= \( %2) (-> %1
                              (z/insert-right '())
                              z/right
                              (z/append-child "")
                              z/down)
                  (= \) %2) (z/up %1)
                  true (z/edit %1 str %2))
                  (z/down (z/seq-zip (list ""))) code)))

(defn infix [tokens]
  (loop [zipper (z/down (z/seq-zip tokens))]
    (if (-?> zipper
          z/right
          z/branch?)
      (recur (let [f (z/node zipper)]
               (-> zipper
                 z/remove
                 z/next
                 (prepend-child f))))
      (if (z/end? zipper)
        (z/root zipper)
        (recur (z/next zipper))))))

(defn infix-math [tokens]
  (z/root (zip-map #(if (#{"+" "-" "*" "/"} (z/node %))
                      (let [right (z/node (z/right %))
                            left (z/node (z/left %))]
                        (-> %
                          z/right
                          z/remove
                          z/left
                          z/remove
                          z/next
                          (z/replace (list (z/node %) left right))))
                      %) (z/seq-zip tokens))))

(defn ban-kwmath [tokens]
  (z/root (zip-map
            #(if-let [op ({"add:" "+",  "mul:" "*"} (z/node %))]
               (let  [right (z/node (z/right %))
                      left (-> %
                             z/right
                             z/remove
                             z/remove
                             z/up
                             z/node)]
                 (-> %
                   z/up
                   (z/replace (list op left right))))
               %)
          (z/seq-zip tokens))))

(defn fnames [tokens]
  (let [code (z/seq-zip tokens)
        overtone-ugen-name (comp
                             #(.replaceAll % "\\.(ar|kr)" ":$1")
                             overtone-ugen-name)]
   (z/root (zip-map #(if (string? (z/node %))
                       (z/edit % overtone-ugen-name)
                       %)
                    code))))

(defn real-types [tokens]
  (z/root
    (zip-map #(if (z/branch? %)
                %
                (if (= "" (z/node %))
                  (z/remove %)
                  (z/replace % (read-string (z/node %)))))
             (z/seq-zip tokens))))

(defmacro sc [code]
    (-> code
      tokenize
      infix
      infix-math
      ban-kwmath
      fnames
      real-types
      (conj 'do)))
