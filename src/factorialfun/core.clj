(ns factorialfun.core
  (:gen-class))

(defn ^:cli f0
  "This is the same as f1, but uses the non-auto-promoting * instead of *'.
   See http://clojuredocs.org/clojure_core/clojure.core/* and
   http://clojuredocs.org/clojure_core/clojure.core/*'
   ArithmeticException integer overflow at (f0 21)"
  [n]
  (println :f0 :n n)
  (if (= 1 n)
    n
    (* n (f0 (dec n)))))

(defn ^:cli f1
  "This is recursive, but consumes stack.
   Note that the recursive call only passes 'n, because the /result/ of the call is then multiplied by n after the fact.
   This cannot be done with tail-call-optimization (TCO), which is the only way to do recursion without consuming the stack.

   Stack overflow (on my MBA, 8G of RAM) at (f1 8352)"
  [n]
  (println :f1 :n n)
  (if (= 1 n)
    n
    (*' n (f1 (dec n)))))

(defn ^:cli f2
  "Works fine up to (e.g.) 80000, stopped testing.
   \"recur is the only non-stack-consuming looping construct in Clojure.\" (http://clojure.org/special_forms#recur)
   When you use 'recur, you need to re-bind a \"collecting\" var of some kind each time.
   This is what allows you to do the multiplication *before* the recursive call, and this is what makes TCO possible.
   "
  ;; This arity-1 fn takes in our intial n, and makes *one*
  ;; non-TCO recursive call on the stack, to initialize our collecting var.
  ([n]
     (println :f2 :n n)
     (f2 n 1))

  ;; This arity-2 fn takes in our initial n and the initialized
  ;; collecting var "prod". 'recur re-binds n and prod as the
  ;; factorial is calculated, returning the final value of prod when n
  ;; is 1.
  ([n prod]
     (println :f2 :n n :prod prod)
     (if (= 1 n)
       prod
       (recur (dec n) (*' n prod)))))


(def f3-seq
  ;; Creates a lazy sequence of factorials.
  ;; With apologies to http://en.wikibooks.org/wiki/Clojure_Programming/Examples/Lazy_Fibonacci
  ((fn f3 [prev n]
     (lazy-seq (cons prev (f3 (*' n prev) (inc n)))))
   1 2))

(defn ^:cli f3-seq-n
  "Convenience fn for testing f3-seq from the CLI.
   The factorial of n is the (dec n) element of the seq."
  [n]
  (nth f3-seq (dec n)))

(defn ^:cli f3-seq-take
  "Convenience fn for testing f3-seq from the CLI."
  [n]
  (take n f3-seq))


(defn -main
  "Takes in two strings: the name of a fn to call, and the arg for
   that fn.

   Gets the symbol of the first string and resolves it to a namespace.

   Gets the symbol of 'f, rebinds 'f to the symbol value.

   Resolves 'f to the 'factorialfun.core namespace, rebinds 'f to the
   resolved 'f, which should now be a fn from above.

   Ensures that 'f implements IFn, throws exception (with a helpful
   message) if not.

   Ensures that 'f is marked (according to our own metadata
   convention) to be accessible from the CLI. See
   http://clojure.org/metadata and
   http://stackoverflow.com/questions/5592306/how-do-i-dynamically-find-metadata-for-a-clojure-function

   Calls 'f with 'n, using 'time to print out the elapsed time.

   Finally, prints out the result.

   Returns nil."
  [f-str n]
  (let [f (symbol f-str)
        f (ns-resolve 'factorialfun.core f)
        _ (if (not (ifn? f))
            (throw (Exception. (str f-str " is not a function!"))))
        _ (or (:cli (meta f))
              (throw (Exception. (str f-str " is not callable from the CLI."))))
        n (Integer. n)
        factorial (time (f n))]
    (println
     f-str "of" n "is"
     factorial)))

(comment
  ;; You must test with strings.
  (factorialfun.core/-main "f1" "4")
  (factorialfun.core/-main "f2" "4")
  )
