;   Copyright (c) Rich Hickey, Stuart Halloway, and contributors.
;   All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns clojure.test.generative
  (:require [clojure.walk :as walk]
            [clojure.test.generative.event :as event]
            [clojure.test.generative.runner :as runner]))

(defn- fully-qualified
  "Qualify a name used in :tag metadata. Unqualified names are
   interpreted in the 'clojure.test.generative.generators, except
   for the fn-building symbols fn and fn*."
  [n]
  (let [ns (cond
            (#{'fn*} n) nil
            (#{'fn} n) 'clojure.core
            (namespace n) (namespace n)
            :else 'clojure.test.generative.generators)]
    (if ns
      (symbol (str ns) (name n))
      n)))

(defn- dequote
  "Remove the backquotes used to call out user-namespaced forms."
  [form]
  (walk/prewalk
   #(if (and (sequential? %)
             (= 2 (count %))
             (= 'quote (first %)))
      (second %)
      %)
   form))

(defn- tag->gen
  "Convert tag to source code form for a test data generator."
  [arg]
  (let [form (walk/prewalk (fn [s] (if (symbol? s) (fully-qualified s) s)) (dequote arg))]
    (if (seq? form)
      (list 'fn '[] form) 
      form)))

(defmacro fail
  [& args]
  (with-meta `(event/report-context :type ::fail
                                    :level :warn
                                    ~@args)
    (meta &form)))

(defmacro is
  ([v] (with-meta `(is ~v nil) (meta &form)))
  ([v msg]
     `(let [~'actual ~v ~'expected '~v]
        (if ~'actual
          (do
            (event/report :level :debug :type ::pass)
            ~'actual)
          ~(with-meta
             `(fail ~@(when msg `[:message ~msg]))
             (meta &form))))))

(defmacro defspec
  [name fn-to-test args & validator-body]
  (when-let [missing-tags (->> (map #(list % (-> % meta :tag)) args)
                               (filter (fn [[_ tag]] (nil? tag)))
                               seq)]
    (throw (IllegalArgumentException. (str "Missing tags for " (seq (map first missing-tags)) " in " name))))
  `(defn ~(with-meta name (assoc (meta name)
                            ::inputs (into [] (map #(-> % meta :tag tag->gen eval)  args))))
     ~(into [] (map (fn [a#] (with-meta a# (dissoc (meta a#) :tag))) args))
     (let [~'% (apply ~fn-to-test ~args)]
       ~@validator-body)))



